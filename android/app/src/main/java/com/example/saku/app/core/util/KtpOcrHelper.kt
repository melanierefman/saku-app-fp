package com.example.saku.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.abs

data class KtpOcrResult(
    val nik: String? = null,
    val nama: String? = null,
    val tempatLahir: String? = null,
    val tanggalLahir: String? = null,
    val jenisKelamin: String? = null,
    val alamat: String? = null,
    val rt: String? = null,
    val rw: String? = null,
    val kelurahan: String? = null,
    val kecamatan: String? = null,
    val kotaKabupaten: String? = null,
    val provinsi: String? = null,
    val agama: String? = null,
    val statusPerkawinan: String? = null,
    val pekerjaan: String? = null,
    val rawText: String = ""
)

object KtpOcrHelper {

    private const val TAG = "KtpOcrHelper"

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    suspend fun recognizeTextFromUri(context: Context, uri: Uri): KtpOcrResult =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromFilePath(context, uri)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        Log.d(TAG, "ML Kit OCR Raw Text:\n${visionText.text}")
                        val parsed = parseVisionText(visionText)
                        Log.d(TAG, "Parsed KTP Result: $parsed")
                        continuation.resume(parsed)
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "ML Kit OCR Failure", error)
                        continuation.resume(KtpOcrResult(rawText = ""))
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading image from Uri for OCR", e)
                continuation.resume(KtpOcrResult(rawText = ""))
            }
        }

    suspend fun recognizeTextFromBitmap(bitmap: Bitmap): KtpOcrResult =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        Log.d(TAG, "ML Kit OCR Raw Text from Bitmap:\n${visionText.text}")
                        val parsed = parseVisionText(visionText)
                        Log.d(TAG, "Parsed KTP Result: $parsed")
                        continuation.resume(parsed)
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "ML Kit OCR Failure from Bitmap", error)
                        continuation.resume(KtpOcrResult(rawText = ""))
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing Bitmap for OCR", e)
                continuation.resume(KtpOcrResult(rawText = ""))
            }
        }

    /**
     * Memproses Vision Text menggunakan analisis spasial 2-Kolom (Bounding Box)
     * Menggabungkan Label di kolom kiri dan Nilai di kolom kanan pada baris Y yang sejajar.
     */
    fun parseVisionText(visionText: Text): KtpOcrResult {
        if (visionText.text.isBlank()) return KtpOcrResult(rawText = "")

        val allLines = visionText.textBlocks.flatMap { it.lines }
        if (allLines.isEmpty()) {
            return parseKtpText(visionText.text)
        }

        // Kelompokkan baris berdasarkan posisi Y (vertikal) yang berdekatan
        val lineClusters = mutableListOf<MutableList<Text.Line>>()
        val sortedByTop = allLines.sortedBy { it.boundingBox?.top ?: 0 }

        for (line in sortedByTop) {
            val lineTop = line.boundingBox?.top ?: 0
            val lineBottom = line.boundingBox?.bottom ?: 0
            val lineCenterY = (lineTop + lineBottom) / 2
            val lineHeight = (lineBottom - lineTop).coerceAtLeast(16)
            val threshold = (lineHeight * 0.75f).toInt().coerceAtLeast(14)

            val matchedCluster = lineClusters.find { cluster ->
                val clusterAvgCenterY = cluster.map {
                    val t = it.boundingBox?.top ?: 0
                    val b = it.boundingBox?.bottom ?: 0
                    (t + b) / 2
                }.average()
                abs(clusterAvgCenterY - lineCenterY) < threshold
            }

            if (matchedCluster != null) {
                matchedCluster.add(line)
            } else {
                lineClusters.add(mutableListOf(line))
            }
        }

        // Urutkan setiap cluster dari kiri ke kanan (berdasarkan BoundingBox.left) lalu gabungkan teksnya
        val spatiallyMergedLines = lineClusters.map { cluster ->
            cluster.sortBy { it.boundingBox?.left ?: 0 }
            cluster.joinToString(" ") { it.text.trim() }
        }

        Log.d(TAG, "Spatially Merged Lines:\n${spatiallyMergedLines.joinToString("\n")}")

        // Gabungkan hasil analisis spasial dengan hasil raw text untuk akurasi maksimal
        val combinedText = spatiallyMergedLines.joinToString("\n") + "\n" + visionText.text
        return parseKtpText(combinedText)
    }

    fun parseKtpText(text: String): KtpOcrResult {
        if (text.isBlank()) return KtpOcrResult(rawText = text)

        val rawLines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

        var nik: String? = null
        var nama: String? = null
        var tempatLahir: String? = null
        var tanggalLahir: String? = null
        var jenisKelamin: String? = null
        var alamat: String? = null
        var rt: String? = null
        var rw: String? = null
        var kelurahan: String? = null
        var kecamatan: String? = null
        var kotaKabupaten: String? = null
        var provinsi: String? = null
        var agama: String? = null
        var statusPerkawinan: String? = null
        var pekerjaan: String? = null

        // 1. Ekstraksi Header: Provinsi & Kota / Kabupaten
        for (line in rawLines) {
            val upper = line.uppercase()
            if (upper.contains("PROVINSI") && provinsi == null) {
                val candidate = cleanTextValue(upper.substringAfter("PROVINSI"))
                if (candidate.isNotBlank() && !candidate.contains("KABUPATEN") && !candidate.contains("KOTA")) {
                    provinsi = "PROVINSI $candidate"
                }
            }
            if ((upper.contains("KABUPATEN") || upper.contains("KOTA")) && !upper.contains("PROVINSI") && kotaKabupaten == null) {
                val prefix = if (upper.contains("KABUPATEN")) "KABUPATEN" else "KOTA"
                val value = cleanTextValue(upper.substringAfter(prefix))
                if (value.isNotBlank() && value.length >= 3) {
                    kotaKabupaten = "$prefix $value"
                }
            }
        }

        // 2. Ekstraksi NIK (16 Digit) - Multi Strategy
        // Strategi A: Cari baris yang mengandung 'NIK' / 'N1K' / 'N!K' / 'N/K'
        for (line in rawLines) {
            val upper = line.uppercase()
            if (upper.contains("NIK") || upper.contains("N1K") || upper.contains("N!K") || upper.contains("N/K")) {
                val marker = when {
                    upper.contains("NIK") -> "NIK"
                    upper.contains("N1K") -> "N1K"
                    upper.contains("N!K") -> "N!K"
                    else -> "N/K"
                }
                val afterNik = upper.substringAfter(marker)
                val sanitized = sanitizeDigits(afterNik)
                val match = Regex("""\d{16}""").find(sanitized)
                if (match != null) {
                    nik = match.value
                    break
                }
            }
        }

        // Strategi B: Scan setiap token kata atau digit sequence (termasuk yang berjarak spasi)
        if (nik == null) {
            val noSpacesText = text.replace(Regex("""(?<=\d)\s+(?=\d)"""), "")
            val allSanitized = sanitizeDigits(noSpacesText)
            val match = Regex("""\d{16}""").find(allSanitized)
            if (match != null) {
                nik = match.value
            }
        }

        // Strategi C: Scan token panjang 15-17
        if (nik == null) {
            val words = text.split(Regex("""[\s:;=\-_/]+"""))
            for (w in words) {
                if (w.length in 15..17) {
                    val cleaned = sanitizeDigits(w)
                    if (cleaned.length == 16) {
                        nik = cleaned
                        break
                    }
                }
            }
        }

        // 3. Ekstraksi Data Identitas per Baris
        for (i in rawLines.indices) {
            val line = rawLines[i]
            val upper = line.uppercase()

            // A. Nama
            if ((upper.contains("NAMA") || upper.startsWith("NAM ") || upper.contains("NARNA")) && nama == null) {
                val marker = when {
                    upper.contains("NAMA") -> "NAMA"
                    upper.contains("NARNA") -> "NARNA"
                    else -> "NAM"
                }
                var candidate = cleanNameValue(upper.substringAfter(marker))
                candidate = stripNextFieldKeywords(candidate)
                if (candidate.length >= 3 && !isHeaderOrLabel(candidate)) {
                    nama = candidate
                } else if (i + 1 < rawLines.size) {
                    val nextLineCandidate = stripNextFieldKeywords(cleanNameValue(rawLines[i + 1].uppercase()))
                    if (nextLineCandidate.length >= 3 && !isHeaderOrLabel(nextLineCandidate)) {
                        nama = nextLineCandidate
                    }
                }
            }

            // B. Tempat & Tanggal Lahir
            if ((upper.contains("TEMPAT") || upper.contains("LAHIR") || upper.contains("TGL LAHIR")) && (tempatLahir == null || tanggalLahir == null)) {
                val marker = when {
                    upper.contains("LAHIR") -> "LAHIR"
                    upper.contains("TEMPAT") -> "TEMPAT"
                    else -> "TGL"
                }
                val content = cleanTextValue(upper.substringAfter(marker))
                val dateMatch = Regex("""\b(\d{2}[-/]\d{2}[-/]\d{4})\b""").find(content)
                if (dateMatch != null) {
                    tanggalLahir = dateMatch.value.replace('/', '-')
                    val placePart = content.substringBefore(dateMatch.value).replace(",", "").trim()
                    if (placePart.length >= 3 && !isHeaderOrLabel(placePart)) {
                        tempatLahir = placePart
                    }
                } else if (content.contains(",")) {
                    val parts = content.split(",")
                    if (parts[0].trim().length >= 3) tempatLahir = parts[0].trim()
                    if (parts.size > 1 && parts[1].trim().isNotBlank()) tanggalLahir = parts[1].trim()
                }
            }

            // C. Jenis Kelamin
            if (upper.contains("JENIS KELAMIN") || upper.contains("KELAMIN") || upper.contains("LAKI-LAKI") || upper.contains("PEREMPUAN")) {
                if (jenisKelamin == null) {
                    if (upper.contains("LAKI") || upper.contains("LAKI-LAKI") || upper.contains("PRIA")) {
                        jenisKelamin = "LAKI-LAKI"
                    } else if (upper.contains("PEREMPUAN") || upper.contains("WANITA")) {
                        jenisKelamin = "PEREMPUAN"
                    }
                }
            }

            // D. Alamat Jalan
            if ((upper.contains("ALAMAT") || upper.startsWith("ALAM")) && alamat == null) {
                val marker = if (upper.contains("ALAMAT")) "ALAMAT" else "ALAM"
                val candidate = cleanTextValue(upper.substringAfter(marker))
                val filtered = stripNextFieldKeywords(candidate)
                if (filtered.length >= 3 && !filtered.contains("RT") && !filtered.contains("RW")) {
                    alamat = filtered
                } else if (i + 1 < rawLines.size && !rawLines[i + 1].uppercase().contains("RT")) {
                    val nextCandidate = stripNextFieldKeywords(cleanTextValue(rawLines[i + 1].uppercase()))
                    if (!isHeaderOrLabel(nextCandidate)) {
                        alamat = nextCandidate
                    }
                }
            } else if ((upper.startsWith("JL.") || upper.startsWith("JALAN") || upper.startsWith("JL ") || upper.startsWith("KP.") || upper.startsWith("KAMPUNG") || upper.startsWith("DUSUN")) && alamat == null) {
                alamat = stripNextFieldKeywords(cleanTextValue(upper))
            }

            // E. RT / RW
            if ((upper.contains("RT") || upper.contains("RW")) && (rt == null || rw == null)) {
                val rtRwRegex = Regex("""(?:RT|R[TI1])\s*[:;.]?\s*([0-9ODIlL]{1,3})\s*(?:/|RW|R[WV])\s*[:;.]?\s*([0-9ODIlL]{1,3})""", RegexOption.IGNORE_CASE)
                val match = rtRwRegex.find(upper)
                if (match != null) {
                    rt = sanitizeDigits(match.groupValues[1]).padStart(3, '0')
                    rw = sanitizeDigits(match.groupValues[2]).padStart(3, '0')
                } else {
                    val singleRtRegex = Regex("""(?:RT|R[TI1])\s*[:;.]?\s*([0-9ODIlL]{1,3})""", RegexOption.IGNORE_CASE)
                    val singleRwRegex = Regex("""(?:RW|R[WV])\s*[:;.]?\s*([0-9ODIlL]{1,3})""", RegexOption.IGNORE_CASE)
                    singleRtRegex.find(upper)?.let { rt = sanitizeDigits(it.groupValues[1]).padStart(3, '0') }
                    singleRwRegex.find(upper)?.let { rw = sanitizeDigits(it.groupValues[1]).padStart(3, '0') }
                }
            }

            // F. Kelurahan / Desa
            if ((upper.contains("KEL") || upper.contains("DESA")) && kelurahan == null && !upper.contains("KECAMATAN")) {
                val marker = when {
                    upper.contains("KELURAHAN") -> "KELURAHAN"
                    upper.contains("KEL/DESA") -> "KEL/DESA"
                    upper.contains("KEL.") -> "KEL."
                    upper.contains("KEL ") -> "KEL "
                    else -> "DESA"
                }
                val candidate = cleanTextValue(upper.substringAfter(marker))
                val filtered = stripNextFieldKeywords(candidate)
                if (filtered.isNotBlank() && !isHeaderOrLabel(filtered)) kelurahan = filtered
            }

            // G. Kecamatan
            if ((upper.contains("KECAMATAN") || upper.contains("KEC.") || upper.startsWith("KEC ")) && kecamatan == null) {
                val marker = if (upper.contains("KECAMATAN")) "KECAMATAN" else if (upper.contains("KEC.")) "KEC." else "KEC"
                val candidate = cleanTextValue(upper.substringAfter(marker))
                val filtered = stripNextFieldKeywords(candidate)
                if (filtered.isNotBlank() && !isHeaderOrLabel(filtered)) kecamatan = filtered
            }

            // H. Agama
            if (upper.contains("AGAMA") && agama == null) {
                val cand = cleanTextValue(upper.substringAfter("AGAMA"))
                val match = listOf("ISLAM", "KRISTEN", "KATOLIK", "HINDU", "BUDDHA", "KONGHUCU").find { cand.contains(it) }
                if (match != null) agama = match
            }

            // I. Status Perkawinan
            if ((upper.contains("STATUS") || upper.contains("PERKAWINAN")) && statusPerkawinan == null) {
                if (upper.contains("BELUM KAWIN") || upper.contains("BELUM MENIKAH")) {
                    statusPerkawinan = "BELUM KAWIN"
                } else if (upper.contains("KAWIN") || upper.contains("MENIKAH")) {
                    statusPerkawinan = "KAWIN"
                } else if (upper.contains("CERAI HIDUP")) {
                    statusPerkawinan = "CERAI HIDUP"
                } else if (upper.contains("CERAI MATI")) {
                    statusPerkawinan = "CERAI MATI"
                }
            }

            // J. Pekerjaan
            if (upper.contains("PEKERJAAN") && pekerjaan == null) {
                val cand = cleanTextValue(upper.substringAfter("PEKERJAAN"))
                val standardJobs = listOf(
                    "KARYAWAN SWASTA", "PEGAWAI NEGERI SIPIL", "PNS", "WIRASWASTA",
                    "PELAJAR/MAHASISWA", "PELAJAR / MAHASISWA", "BELUM/TIDAK BEKERJA",
                    "IBU RUMAH TANGGA", "BURUH HARIAN LEPAS", "BURUH", "DOKTER",
                    "GURU", "TNI", "POLRI", "PEDAGANG", "PETANI", "PENSIUNAN"
                )
                val matched = standardJobs.find { cand.contains(it) }
                if (matched != null) {
                    pekerjaan = matched
                } else {
                    val filtered = stripNextFieldKeywords(cand)
                    if (filtered.length >= 3 && !isHeaderOrLabel(filtered)) {
                        pekerjaan = filtered
                    }
                }
            }
        }

        return KtpOcrResult(
            nik = nik,
            nama = nama,
            tempatLahir = tempatLahir,
            tanggalLahir = tanggalLahir,
            jenisKelamin = jenisKelamin,
            alamat = alamat,
            rt = rt,
            rw = rw,
            kelurahan = kelurahan,
            kecamatan = kecamatan,
            kotaKabupaten = kotaKabupaten,
            provinsi = provinsi,
            agama = agama,
            statusPerkawinan = statusPerkawinan,
            pekerjaan = pekerjaan,
            rawText = text
        )
    }

    private fun cleanTextValue(value: String): String {
        return value
            .replace(Regex("^[:;=._/-]+"), "")
            .replace(Regex("[^A-Za-z0-9\\s.,/-]"), "")
            .trim()
    }

    private fun cleanNameValue(value: String): String {
        return value
            .replace(Regex("^[:;=._/-]+"), "")
            .replace(Regex("[^A-Za-z\\s.,'-]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun isHeaderOrLabel(value: String): Boolean {
        val upper = value.uppercase()
        return upper.contains("PROVINSI") || upper.contains("KABUPATEN") || upper.contains("KOTA") ||
                upper.contains("NIK") || upper.contains("TEMPAT") || upper.contains("LAHIR") ||
                upper.contains("AGAMA") || upper.contains("GOL DARAH") || upper.contains("STATUS") ||
                upper.contains("REPUBLIK") || upper.contains("INDONESIA") || upper.contains("KARTU")
    }

    private fun stripNextFieldKeywords(value: String): String {
        val keywords = listOf(
            "TEMPAT", "LAHIR", "TGL", "JENIS", "KELAMIN", "GOL", "DARAH",
            "ALAMAT", "RT", "RW", "KEL", "DESA", "KECAMATAN", "AGAMA",
            "STATUS", "PERKAWINAN", "PEKERJAAN", "KEWARGANEGARAAN", "BERLAKU", "NIK"
        )
        var result = value
        for (kw in keywords) {
            if (result.contains(" $kw") || result.contains(":$kw") || result.contains("-$kw") || result.contains("/$kw")) {
                result = result.substringBefore(" $kw").substringBefore(":$kw").substringBefore("-$kw").substringBefore("/$kw")
            }
        }
        return result.trim()
    }

    private fun sanitizeDigits(input: String): String {
        return input
            .replace('O', '0')
            .replace('o', '0')
            .replace('D', '0')
            .replace('Q', '0')
            .replace('U', '0')
            .replace('I', '1')
            .replace('l', '1')
            .replace('L', '1')
            .replace('!', '1')
            .replace('|', '1')
            .replace('i', '1')
            .replace('J', '1')
            .replace('Z', '2')
            .replace('z', '2')
            .replace('E', '3')
            .replace('A', '4')
            .replace('S', '5')
            .replace('s', '5')
            .replace('G', '6')
            .replace('b', '6')
            .replace('T', '7')
            .replace('B', '8')
            .replace('g', '9')
            .replace('q', '9')
            .filter { it.isDigit() }
    }
}