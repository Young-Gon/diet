package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Transaction
import com.gondev.clog.CLog
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.database.entity.RecommendWorkoutRelationshipEntity
import com.seedit.diet.database.entity.WorkoutEntity
import com.seedit.diet.util.ioThread
import java.text.SimpleDateFormat
import java.util.*

class RecommendWorkoutRelationshipViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application)
{
	private val workoutDao=database.workoutDao()
	private val recommendWorkoutDao=database.recommendWorkoutDao()
	private val recommendWorkoutRelationshipDao=database.recommendWorkoutRelationshipDao()

	private lateinit var recommendObservable: LiveData<List<RecommendWorkoutEntity>>
	private var recommendMediatorLiveData = MediatorLiveData<List<RecommendWorkoutEntity>>()

	private lateinit var workoutObservable: LiveData<List<WorkoutEntity>>
	private var workoutMediatorLiveData = MediatorLiveData<List<WorkoutEntity>>()

	fun find(calendar: Calendar) {
		CLog.d("find "+ SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.time),5)
		if(::recommendObservable.isInitialized)
			recommendMediatorLiveData.removeSource(recommendObservable)

		recommendObservable=recommendWorkoutRelationshipDao.find(calendar.time)

		recommendMediatorLiveData.addSource(recommendObservable) { recommendMediatorLiveData.value = it }

		if(::workoutObservable.isInitialized)
			workoutMediatorLiveData.removeSource(workoutObservable)

		workoutObservable=workoutDao.findByDate(calendar.time)
		workoutMediatorLiveData.addSource(workoutObservable){ workoutMediatorLiveData.value=it }
	}

	@Transaction
	fun insertNewRecommendWorkout(calendar: Calendar) = ioThread{
		recommendWorkoutDao.findOneByRandom().let {
			recommendWorkoutRelationshipDao.insert(RecommendWorkoutRelationshipEntity(it.id,calendar.time))
		}
	}

	fun observeForRecommend(owner: LifecycleOwner, observer: Observer<List<RecommendWorkoutEntity>>) {
		recommendMediatorLiveData.observe(owner, observer)
	}

	fun observeForWorkout(owner: LifecycleOwner, observer: Observer<List<WorkoutEntity>>) {
		workoutMediatorLiveData.observe(owner, observer)
	}
}