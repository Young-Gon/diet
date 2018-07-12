package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Transaction
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.database.entity.RecommendWorkoutRelationshipEntity
import com.seedit.diet.util.ioThread
import java.util.*

class RecommendWorkoutRelationshipViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application)
{
	private val recommendWorkoutDao=database.recommendWorkoutDao()
	private val recommendWorkoutRelationshipDao=database.recommendWorkoutRelationshipDao()

	private lateinit var recommendObservable: LiveData<List<RecommendWorkoutEntity>>
	private var recommendMediatorLiveData = MediatorLiveData<List<RecommendWorkoutEntity>>()

	fun find(calendar: Calendar) {
		if(::recommendObservable.isInitialized)
			recommendMediatorLiveData.removeSource(recommendObservable)

		recommendObservable=recommendWorkoutRelationshipDao.find(calendar.time)

		recommendMediatorLiveData.addSource(recommendObservable) { recommendMediatorLiveData.value = it }
	}

	@Transaction
	fun insertNewRecommedWorkout()= ioThread{
		recommendWorkoutDao.findAll().let {
			recommendWorkoutRelationshipDao.insert(RecommendWorkoutRelationshipEntity(it.shuffled()[0].id))
		}
	}

	fun observeForRecommend(owner: LifecycleOwner, observer: Observer<List<RecommendWorkoutEntity>>) {
		recommendMediatorLiveData.observe(owner, observer)
	}
}