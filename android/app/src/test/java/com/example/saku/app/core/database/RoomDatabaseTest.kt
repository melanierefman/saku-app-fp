package com.example.saku.app.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.saku.app.core.database.dao.CustomerDao
import com.example.saku.app.core.database.dao.LoanDao
import com.example.saku.app.core.database.dao.NotificationDao
import com.example.saku.app.core.database.entity.CustomerProfileEntity
import com.example.saku.app.core.database.entity.LoanApplicationEntity
import com.example.saku.app.core.database.entity.NotificationEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var customerDao: CustomerDao
    private lateinit var loanDao: LoanDao
    private lateinit var notificationDao: NotificationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        customerDao = db.customerDao()
        loanDao = db.loanDao()
        notificationDao = db.notificationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `insert and retrieve customer profile from Room`() = runTest {
        val profile = CustomerProfileEntity(
            id = "cust-1",
            nik = "3201234567890001",
            nama = "Budi Santoso",
            username = "budisantoso",
            email = "budi@example.com",
            noHp = "081234567890",
            namaBank = "BCA",
            noRekening = "1234567890",
            namaRekening = "Budi Santoso",
            isKycVerified = true,
            statusVerifikasi = "VERIFIED",
            catatanVerifikasi = null,
            pekerjaan = "Karyawan Swasta",
            tempatKerja = "PT Tech",
            statusPekerjaan = "TETAP",
            penghasilanBulanan = 15000000.0,
            lamaBekerjaBulan = 24,
            totalCicilanLainBulanan = 0.0,
            totalPlafond = 50000000.0,
            usedPlafond = 10000000.0,
            availablePlafond = 40000000.0,
            tierPlafond = "GOLD",
            sukuBunga = 1.25,
            biayaAdmin = 50000.0,
            alamatKtpFormatted = "Jakarta Selatan",
            alamatDomisiliFormatted = "Jakarta Selatan",
            fotoKtp = null,
            fotoSelfie = null,
            createdDate = "2026-01-01"
        )

        customerDao.insertProfile(profile)
        val loaded = customerDao.getProfile()

        assertNotNull(loaded)
        assertEquals("Budi Santoso", loaded?.nama)
        assertEquals(50000000.0, loaded?.totalPlafond)
        assertEquals(true, loaded?.isKycVerified)
    }

    @Test
    fun `insert and retrieve loan applications from Room`() = runTest {
        val loan = LoanApplicationEntity(
            id = "loan-1",
            nomorPengajuan = "SAKU-2026-001",
            customerId = "cust-1",
            namaCustomer = "Budi Santoso",
            branchId = "BR-01",
            namaCabang = "Cabang Utama",
            kotaCabang = "Jakarta",
            jumlahPinjaman = 5000000.0,
            tenorBulan = 6,
            tujuanPinjaman = "Modal Usaha",
            bunga = 1.5,
            biayaAdmin = 50000.0,
            estimasiAngsuranBulanan = 908333.0,
            skorKesehatan = 85,
            statusPengajuan = "DISETUJUI",
            catatanReview = null,
            createdDate = "2026-02-01",
            updatedDate = "2026-02-02"
        )

        loanDao.insertLoans(listOf(loan))
        val list = loanDao.getAllLoans()

        assertEquals(1, list.size)
        assertEquals("SAKU-2026-001", list[0].nomorPengajuan)
        assertEquals(5000000.0, list[0].jumlahPinjaman)
    }

    @Test
    fun `insert and retrieve notifications from Room`() = runTest {
        val notif = NotificationEntity(
            id = "notif-1",
            type = "LOAN_STATUS",
            channel = "PUSH",
            judul = "Pengajuan Disetujui",
            pesan = "Pinjaman Anda telah disetujui",
            status = "SENT",
            isRead = false,
            pengajuanPinjamanId = "loan-1",
            createdDate = "2026-02-01"
        )

        notificationDao.insertNotifications(listOf(notif))
        val list = notificationDao.getAllNotifications()

        assertEquals(1, list.size)
        assertEquals("Pengajuan Disetujui", list[0].judul)

        notificationDao.markAsRead("notif-1")
        val updated = notificationDao.getAllNotifications()
        assertEquals(true, updated[0].isRead)
    }
}
