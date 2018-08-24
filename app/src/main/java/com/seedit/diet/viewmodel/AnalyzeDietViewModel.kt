package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.gondev.clog.CLog
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.BodyEntity
import com.seedit.diet.database.entity.DietCaloriesPOJO
import com.seedit.diet.database.entity.ProfileEntity
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat

class AnalyzeDietViewModel (application: Application, database: AppDatabase) : AndroidViewModel(application)
{
	private var bodyDao=database.bodyDao()
	private val dietDao=database.dietDao()
	private val workoutDao=database.workoutDao()
	private val profileDao=database.profileDao()

	fun findBody(owner: LifecycleOwner, observer: Observer<List<BodyEntity>>) =
		bodyDao.find().observe(owner,observer)

	fun findDiet(owner: LifecycleOwner, observer: Observer<List<DietCaloriesPOJO>>) =
		dietDao.findCaloriesGroupByDate().observe(owner,observer)

	fun findWorkout(owner: LifecycleOwner, observer: Observer<List<DietCaloriesPOJO>>) =
		workoutDao.findCaloriesGroupByDate().observe(owner,observer)

	fun findCalorieDietWorkout(owner: LifecycleOwner, observer: Observer<List<DietCaloriesPOJO>>) =
		workoutDao.findCalorieDietWorkout().observe(owner,observer)

	private val disposable = CompositeDisposable()

	fun findBMI(sdf: SimpleDateFormat, function:(List<Entry>)->Unit) {
		disposable.add(Flowable.zip<ProfileEntity, List<BodyEntity>, List<Entry>>(profileDao.findRx(), bodyDao.findRx(), BiFunction { profile, bodyList ->
			bodyList.map {entity->
				Entry(sdf.parse(entity.date).time.toFloat(), (entity.weight.toFloat() /(profile.height.toFloat()*profile.height.toFloat()))* 10000)
			}
		}).subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(function){ error ->
			CLog.e( "Unable to get username", error)
		})
	}

	override fun onCleared() {
		super.onCleared()
		disposable.clear()
	}
}
