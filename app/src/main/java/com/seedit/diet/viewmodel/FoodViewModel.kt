package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.persistence.room.Transaction
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.FoodEntity
import com.seedit.diet.util.ioThread

class FoodViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application)
{
	private val foodDao=database.foodDao()
	/*private lateinit var observable: LiveData<List<FoodEntity>>
	private var mediatorLiveData = MediatorLiveData<List<FoodEntity>>()

	fun find(keyword: String) {
		if(::observable.isInitialized)
			mediatorLiveData.removeSource(observable)

		observable=foodDao.find(keyword)
		mediatorLiveData.addSource(observable){mediatorLiveData.value=it}
	}

	fun observe(owner: LifecycleOwner, observer: Observer<List<FoodEntity>>) {
		mediatorLiveData.observe(owner,observer)
	}*/
	fun find(keyword: String) = foodDao.find(keyword)

	@Transaction
	fun insert(entity: FoodEntity)= ioThread {
		foodDao.insertAll(arrayOf(entity))
	}
}