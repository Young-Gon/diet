package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.util.AppExecutors

class ProfileViewModel(application: Application,database: AppDatabase): AndroidViewModel(application)
{
    private val profileDao=database.profileDao()
    // set by default null, until we get data from the database.observable
    val observable: LiveData<List<ProfileEntity>> = profileDao.findAll()

    fun insert(profile: ProfileEntity) {
        AppExecutors.diskIO.execute {
                profileDao.insert(profile)
        }
    }

    fun getProfile()=
        observable.value?.let {
            if(it.isNotEmpty())
                it[0]
            else ProfileEntity(0)
        }

    fun update()=observable.value?.let {
        if(it.isNotEmpty())
            insert(it[0])
    }
}