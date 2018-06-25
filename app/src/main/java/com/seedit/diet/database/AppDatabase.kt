package com.seedit.diet.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.gondev.clog.CLog
import com.seedit.diet.database.converter.DateConverter
import com.seedit.diet.database.converter.UriConverter
import com.seedit.diet.database.dao.ProfileDao
import com.seedit.diet.database.entity.ProfileEntity

@Database(entities = arrayOf(ProfileEntity::class), version = 1)
@TypeConverters(DateConverter::class, UriConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "com.seedit.diet.db")
                        .addCallback(object : RoomDatabase.Callback()
                        {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                //TODO 초기 디비 값 설정
                                CLog.i("디비 생성")
                            }
                        })
                        .build()
    }
}