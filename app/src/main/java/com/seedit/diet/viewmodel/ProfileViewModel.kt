package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.seedit.diet.database.dao.ProfileDao
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.util.AppExecutors

class ProfileViewModel(application: Application, private val profileDao: ProfileDao): AndroidViewModel(application)
{
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

    fun setPorfile(profile: ProfileEntity)=
            profileDao.insert(profile)

}