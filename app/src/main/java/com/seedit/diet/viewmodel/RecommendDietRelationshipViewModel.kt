package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Transaction
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.RecommendDietEntity
import com.seedit.diet.database.entity.RecommendDietRelationshipEntity
import com.seedit.diet.util.ioThread
import java.util.*

class RecommendDietRelationshipViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application)
{
	private val dietDao=database.dietDao()
	private val recommendDietDao=database.recommendDietDao()
	private val recommendDietRelationshipDao=database.recommendDietRelationshipDao()

	private lateinit var recommendObservable: LiveData<List<RecommendDietEntity>>
	private var recommendMediatorLiveData = MediatorLiveData<List<RecommendDietEntity>>()

	private lateinit var dietObservable: LiveData<List<DietEntity>>
	private var dietMediatorLiveData = MediatorLiveData<List<DietEntity>>()

	fun find(calendar: Calendar) {
		if(::recommendObservable.isInitialized)
			recommendMediatorLiveData.removeSource(recommendObservable)

		recommendObservable=recommendDietRelationshipDao.find(calendar.time)
		recommendMediatorLiveData.addSource(recommendObservable) { recommendMediatorLiveData.value = it }

		if(::dietObservable.isInitialized)
			dietMediatorLiveData.removeSource(dietObservable)

		dietObservable=dietDao.findByDate(calendar.time)
		dietMediatorLiveData.addSource(dietObservable){ dietMediatorLiveData.value=it }
	}

	@Transaction
	fun createNewRecommendDiet(calendar: Calendar) = ioThread {
		recommendDietDao.findOneByRandom().let {
			recommendDietRelationshipDao.insert(RecommendDietRelationshipEntity(it.id,calendar.time))
		}
	}

	fun observeForRecommend(owner: LifecycleOwner, observer: Observer<List<RecommendDietEntity>>) {
		recommendMediatorLiveData.observe(owner, observer)
	}

	fun observeForDiet(owner: LifecycleOwner, observer: Observer<List<DietEntity>>) {
		dietMediatorLiveData.observe(owner, observer)
	}

	@Transaction
	fun insertDiet(entity: DietEntity) = ioThread {
		entity.id=dietDao.insertAll(arrayOf(entity))[0]
	}
}