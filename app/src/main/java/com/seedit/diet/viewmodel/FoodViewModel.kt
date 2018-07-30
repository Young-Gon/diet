package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.persistence.room.Transaction
import android.database.sqlite.SQLiteConstraintException
import com.gondev.clog.CLog
import com.seedit.diet.adapter.DataSet
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.DietFoodRelationEntity
import com.seedit.diet.database.entity.DietWithFood
import com.seedit.diet.database.entity.FoodEntity
import com.seedit.diet.util.ioThread
import com.seedit.diet.util.mainThread

class FoodViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application), DataSet<FoodEntity>
{
	private val dietDao=database.dietDao()
	private val foodDao=database.foodDao()
	private val dietFoodDao=database.dietFoodDao()
	private lateinit var observable: LiveData<List<DietWithFood>>
	private var mediatorLiveData = MediatorLiveData<List<DietWithFood>>()

	override fun findCursor(keyword: String) = foodDao.find(keyword)

	fun findDietFoodByDietID(dietID:Long)
	{
		if(::observable.isInitialized)
			mediatorLiveData.removeSource(observable)

		observable=dietFoodDao.findByDietID(dietID)
		mediatorLiveData.addSource(observable){mediatorLiveData.value=it}
	}

	fun observe(owner: LifecycleOwner, observer: Observer<List<DietWithFood>>) {
		mediatorLiveData.observe(owner,observer)
	}

	@Transaction
	fun insert(entity: FoodEntity)= ioThread {
		entity._id=foodDao.insertAll(arrayOf(entity))[0]
	}

	@Transaction
	fun insertDietFoodRelationship(dietFoodEntity: DietFoodRelationEntity) = ioThread {
		CLog.d(dietFoodEntity.toString())

		dietFoodDao.update(dietFoodEntity)
	}

	@Transaction
	fun insertDietFoodRelationship(dietEntity: DietEntity, foodEntity: FoodEntity, count: Int)= ioThread {
		if(dietEntity.id==0L) {
			dietEntity.id = dietDao.insertAll(arrayOf(dietEntity))[0]
			CLog.d("insert diet id=${dietEntity.id}")
			mainThread {
				findDietFoodByDietID(dietEntity.id)
			}
		}

		CLog.d(dietEntity.toString())
		try {
			dietFoodDao.insertAll(arrayOf(DietFoodRelationEntity(dietEntity.id,foodEntity._id,count)))
		} catch (e: SQLiteConstraintException) {
			dietFoodDao.update(dietEntity.id,foodEntity._id)
		}
	}

	@Transaction
	fun insert(dietEntity: DietEntity) = ioThread{
		CLog.d("insert diet id=${dietEntity.id}")
		dietDao.insertAll(arrayOf(dietEntity))
	}

	@Transaction
	fun delete(dietFood: DietFoodRelationEntity) =ioThread{
		dietFoodDao.delete(dietFood)
	}

	fun checkIfDeleteItselfOrNot(dietEntity: DietEntity) = with(observable.value){ ioThread{
		if(this ==null || isEmpty())
		{
			if(dietEntity.id!=0L)
				dietDao.delete(dietEntity)
		}
	}}
}