package com.seedit.diet.database.repository

import android.content.Context
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.dao.ProfileDao
import com.seedit.diet.database.dao.RecommendDietDao

object Repository {
    fun provideProfileDataSource(context: Context): ProfileDao =
        AppDatabase.getInstance(context).profileDao()

    fun provideRecommendDataSource(context: Context): RecommendDietDao =
            AppDatabase.getInstance(context).recommendDietDao()
}