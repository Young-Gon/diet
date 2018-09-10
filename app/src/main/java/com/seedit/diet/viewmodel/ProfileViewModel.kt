package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Transaction
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.util.ioThread

class ProfileViewModel(application: Application,database: AppDatabase): AndroidViewModel(application)
{
    private val profileDao=database.profileDao()
    private val bodyDao=database.bodyDao()
    private val dietDao=database.dietDao()
    private val recommendDietRelationshipDao=database.recommendDietRelationshipDao()
    private val recommendWorkoutRelationshipDao=database.recommendWorkoutRelationshipDao()
    private val workoutDao=database.workoutDao()
    private val workoutRelationshipDao=database.workoutRelationshipDao()


    @Transaction
    fun deleteAllData() = ioThread {
        profileDao.deleteAll()
        bodyDao.deleteAll()
        dietDao.deleteAll()
        recommendDietRelationshipDao.deleteAll()
        recommendWorkoutRelationshipDao.deleteAll()
        workoutDao.deleteAll()
        workoutRelationshipDao.deleteAll()
    }

    // set by default null, until we get data from the database.observable
    val observable: LiveData<List<ProfileEntity>> = profileDao.findAll()

    fun insert(profile: ProfileEntity) = ioThread {
        profileDao.insert(profile)
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