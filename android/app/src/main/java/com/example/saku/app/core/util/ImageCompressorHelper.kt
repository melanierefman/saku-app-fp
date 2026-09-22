package com.example.saku.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

object ImageCompressorHelper {

    private const val TAG = "ImageCompressorHelper"

    /**
     * Membuat Uri sementara untuk kamera bawaan HP menggunakan FileProvider.
     */
    fun createTempPictureUri(context: Context, prefix: String = "camera_capture_"): Uri {
        val cacheFolder = context.externalCacheDir ?: context.cacheDir
        val tempFile = File.createTempFile(
            "${prefix}${System.currentTimeMillis()}_",
            ".jpg",
            cacheFolder
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    /**
     * Mengompresi dan me-resize gambar dari Uri menjadi ByteArray JPEG berukuran ringan (~300KB - 800KB).
     * Otomatis mengoreksi orientasi rotasi EXIF dari kamera HP.
     *
     * @param context Android context
     * @param uri Uri file foto
     * @param maxDimension Dimensi terpanjang (lebar/tinggi) maksimal, default 1920px (Full HD)
     * @param quality Kualitas kompresi JPEG (1-100), default 80%
     * @return ByteArray JPEG terkompresi
     */
    fun compressImageUri(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1920,
        quality: Int = 80
    ): ByteArray? {
        return try {
            val contentResolver = context.contentResolver

            // 1. Baca Dimensi Asli tanpa memuat seluruh gambar ke RAM (inJustDecodeBounds)
            var input: InputStream? = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            input?.close()

            val originalWidth = options.outWidth
            val originalHeight = options.outHeight
            if (originalWidth <= 0 || originalHeight <= 0) {
                Log.w(TAG, "Gagal membaca dimensi gambar dari Uri: $uri")
                return contentResolver.openInputStream(uri)?.readBytes()
            }

            // 2. Hitung inSampleSize untuk efisiensi memori
            var sampleSize = 1
            val maxOriginal = max(originalWidth, originalHeight)
            while (maxOriginal / (sampleSize * 2) >= maxDimension) {
                sampleSize *= 2
            }

            // 3. Decode Bitmap dengan inSampleSize
            input = contentResolver.openInputStream(uri)
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            var decodedBitmap = BitmapFactory.decodeStream(input, null, decodeOptions)
            input?.close()

            if (decodedBitmap == null) {
                Log.w(TAG, "Gagal mendecode Bitmap dari Uri: $uri")
                return contentResolver.openInputStream(uri)?.readBytes()
            }

            // 4. Koreksi Rotasi EXIF
            val rotation = getExifRotation(context, uri)
            if (rotation != 0) {
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                val rotated = Bitmap.createBitmap(decodedBitmap, 0, 0, decodedBitmap.width, decodedBitmap.height, matrix, true)
                if (rotated != decodedBitmap) {
                    decodedBitmap.recycle()
                    decodedBitmap = rotated
                }
            }

            // 5. Scale down tepat ke maxDimension jika masih lebih besar
            val currentMax = max(decodedBitmap.width, decodedBitmap.height)
            if (currentMax > maxDimension) {
                val scale = maxDimension.toFloat() / currentMax.toFloat()
                val targetWidth = (decodedBitmap.width * scale).roundToInt()
                val targetHeight = (decodedBitmap.height * scale).roundToInt()
                val scaled = Bitmap.createScaledBitmap(decodedBitmap, targetWidth, targetHeight, true)
                if (scaled != decodedBitmap) {
                    decodedBitmap.recycle()
                    decodedBitmap = scaled
                }
            }

            // 6. Kompresi JPEG Quality 80%
            val outputStream = ByteArrayOutputStream()
            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val resultBytes = outputStream.toByteArray()

            Log.d(TAG, "Kompresi Selesai: Asli ${originalWidth}x${originalHeight} -> Hasil ${decodedBitmap.width}x${decodedBitmap.height}, Ukuran: ${resultBytes.size / 1024} KB")

            decodedBitmap.recycle()
            resultBytes
        } catch (e: Exception) {
            Log.e(TAG, "Error saat kompresi gambar Uri", e)
            try {
                context.contentResolver.openInputStream(uri)?.readBytes()
            } catch (ex: Exception) {
                null
            }
        }
    }

    /**
     * Mengompresi Bitmap langsung (misal hasil dari capture/crop).
     */
    fun compressBitmap(
        bitmap: Bitmap,
        maxDimension: Int = 1920,
        quality: Int = 80
    ): ByteArray {
        var current = bitmap
        val currentMax = max(current.width, current.height)
        if (currentMax > maxDimension) {
            val scale = maxDimension.toFloat() / currentMax.toFloat()
            val targetWidth = (current.width * scale).roundToInt()
            val targetHeight = (current.height * scale).roundToInt()
            current = Bitmap.createScaledBitmap(current, targetWidth, targetHeight, true)
        }

        val outputStream = ByteArrayOutputStream()
        current.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    private fun getExifRotation(context: Context, uri: Uri): Int {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return 0
            val exif = ExifInterface(input)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            input.close()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }
}