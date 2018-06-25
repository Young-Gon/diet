package com.seedit.diet.database.repository

import android.content.Context
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.dao.ProfileDao

object Repository {
    fun provideProfileDataSource(context: Context): ProfileDao {
        return AppDatabase.getInstance(context).profileDao()
    }
}