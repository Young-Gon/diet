package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.BodyEntity
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.util.ioThread
import java.util.*

class SummaryViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application) {
	private var profileDao=database.profileDao()
	private var bodyDao=database.bodyDao()
	private val dietDao=database.dietDao()
	private val workoutDao=database.workoutDao()

	private lateinit var profileObservable:LiveData<ProfileEntity>
	private lateinit var bodyObservable:LiveData<List<BodyEntity>>

	private lateinit var dietObservable: LiveData<Float>
	private var dietMediatorLiveData = MediatorLiveData<Float>()

	private lateinit var workoutObservable: LiveData<Float>
	private var workoutMediatorLiveData = MediatorLiveData<Float>()

	fun findProfile(owner: LifecycleOwner, observer: Observer<ProfileEntity>) {
		profileObservable=profileDao.find()
		profileObservable.observe(owner, observer)
	}

	fun findBody(owner: LifecycleOwner, observer: Observer<List<BodyEntity>>) {
		bodyObservable=bodyDao.find()
		bodyObservable.observe(owner,observer)
	}

	/*fun insertWeight(date: String, weight: Int) = ioThread{
		try {
			bodyDao.updateWeight(date, weight)
		} catch (e: SQLiteConstraintException) {
			bodyDao.insert(BodyEntity(date,weight,0,null))
		}
	}

	fun insertWater(date: String, water: Int) = ioThread{
		try {
			bodyDao.updateWater(date, water)
		} catch (e:SQLiteConstraintException) {
			bodyDao.insert(BodyEntity(date,0,water,null))
		}
	}*/

	fun insertBody(body: BodyEntity) = ioThread {
		bodyDao.insert(body)
	}

	fun findDiet(calendar: Calendar) {
		if(::dietObservable.isInitialized)
			dietMediatorLiveData.removeSource(dietObservable)

		dietObservable=dietDao.findTotalCalories(calendar.time)
		dietMediatorLiveData.addSource(dietObservable){ dietMediatorLiveData.value=it }
	}

	fun observeForDiet(owner: LifecycleOwner, observer: Observer<Float>) {
		dietMediatorLiveData.observe(owner, observer)
	}

	fun findWorkout(calendar: Calendar) {
		if(::workoutObservable.isInitialized)
			workoutMediatorLiveData.removeSource(workoutObservable)

		workoutObservable=workoutDao.findTotalCalories(calendar.time)
		workoutMediatorLiveData.addSource(workoutObservable){ workoutMediatorLiveData.value=it }
	}

	fun observeForWorkout(owner: LifecycleOwner, observer: Observer<Float>) {
		workoutMediatorLiveData.observe(owner, observer)
	}
}