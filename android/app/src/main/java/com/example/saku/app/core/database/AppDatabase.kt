package com.example.saku.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.saku.app.core.database.dao.CustomerDao
import com.example.saku.app.core.database.dao.LoanDao
import com.example.saku.app.core.database.dao.NotificationDao
import com.example.saku.app.core.database.entity.CustomerProfileEntity
import com.example.saku.app.core.database.entity.LoanApplicationEntity
import com.example.saku.app.core.database.entity.NotificationEntity

@Database(
    entities = [
        CustomerProfileEntity::class,
        LoanApplicationEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun loanDao(): LoanDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        private const val DATABASE_NAME = "saku_app_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
