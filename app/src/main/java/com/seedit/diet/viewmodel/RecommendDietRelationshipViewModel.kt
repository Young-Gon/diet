package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Transaction
import com.gondev.clog.CLog
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

	//private val testObserver=recommendDietRelationshipDao.findAll()

	fun find(calendar: Calendar) {
		val newCal= calendar.clone() as Calendar
		newCal.set(Calendar.HOUR_OF_DAY,0)
		newCal.set(Calendar.MINUTE,0)
		newCal.set(Calendar.SECOND,0)
		val from= newCal.time
		newCal.add(Calendar.DATE,1)
		val to=newCal.time

		CLog.d("to=${to}, from=${from}")
		if(::recommendObservable.isInitialized)
			recommendMediatorLiveData.removeSource(recommendObservable)

		recommendObservable=recommendDietRelationshipDao.find(from,to)
		recommendMediatorLiveData.addSource(recommendObservable) { recommendMediatorLiveData.value = it }

		if(::dietObservable.isInitialized)
			dietMediatorLiveData.removeSource(dietObservable)

		dietObservable=dietDao.findByDate(from, to)
		dietMediatorLiveData.addSource(dietObservable){ dietMediatorLiveData.value=it }

		//testObserver.observeForever { CLog.d("recommendDietRelationshipDao =${it?.get(it.size-1).toString()}") }
	}

	fun createNewRecommendDiet(owner: LifecycleOwner/*, f:(result: RecommendWithDiet)->Unit*/, recommendDay: Date) =
			recommendDietDao.findAll().observe(owner, Observer {it?.let {
				val result = RecommendDietRelationshipEntity(it.shuffled()[0].id,recommendDay)
				insertRecommendDiet(result)
			}})

	fun observeForRecommend(owner: LifecycleOwner, observer: Observer<List<RecommendDietEntity>>) {
		recommendMediatorLiveData.observe(owner, observer)
	}


	@Transaction
	fun insertRecommendDiet(entity: RecommendDietRelationshipEntity) = ioThread {
		recommendDietRelationshipDao.insert(entity)
	}

	fun observeForDiet(owner: LifecycleOwner, observer: Observer<List<DietEntity>>) {
		dietMediatorLiveData.observe(owner, observer)
	}

	/*fun dietObserve(owner: LifecycleOwner, observer: Observer<List<DietEntity>>) {
		dietObservable.observe(owner,observer)
	}*/

	@Transaction
	fun insertDiet(entity: DietEntity) = ioThread {
		entity.id=dietDao.insertAll(arrayOf(entity))[0]
	}
}