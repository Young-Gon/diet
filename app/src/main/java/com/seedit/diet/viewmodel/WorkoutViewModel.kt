package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.persistence.room.Transaction
import com.seedit.diet.adapter.DataSet
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.database.entity.WorkoutEntity
import com.seedit.diet.database.entity.WorkoutWithRecommend
import com.seedit.diet.util.ioThread

class WorkoutViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application), DataSet<RecommendWorkoutEntity>
{
	private val recommendWorkoutDao=database.recommendWorkoutDao()
	private val workoutRelationshipDao=database.workoutRelationship()
	private val workoutDao=database.workoutDao()

	private lateinit var observable: LiveData<List<WorkoutWithRecommend>>
	private var mediatorLiveData = MediatorLiveData<List<WorkoutWithRecommend>>()

	override fun findCursor(keyword: String): List<RecommendWorkoutEntity> =recommendWorkoutDao.find(keyword)

	fun findWorkoutByID(id: Long){
		if(::observable.isInitialized)
			mediatorLiveData.removeSource(observable)

		observable=workoutRelationshipDao.find(id)
		mediatorLiveData.addSource(observable){mediatorLiveData.value=it}
	}

	fun observe(owner: LifecycleOwner, observer: Observer<List<WorkoutWithRecommend>>) {
		mediatorLiveData.observe(owner,observer)
	}

	fun insert(item: RecommendWorkoutEntity) = ioThread {
		recommendWorkoutDao.insertAll(arrayOf(item))
	}

	@Transaction
	fun insert(workEntity: WorkoutEntity, list: List<WorkoutWithRecommend>) = ioThread {
		workoutDao.insert(workEntity).let {id->
			workoutRelationshipDao.insertAll(list.map {
				it.relationship.apply { workoutID=id }
			})
		}
	}

	fun delete(workEntity: WorkoutEntity)= ioThread {
		workoutDao.delete(workEntity)
	}
}
