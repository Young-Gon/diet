package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.database.sqlite.SQLiteConstraintException
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.entity.BodyEntity
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.util.ioThread

class SummaryViewModel(application: Application,database: AppDatabase) : AndroidViewModel(application) {
	private var profileDao=database.profileDao()
	private var bodyDao=database.bodyDao()

	private lateinit var profileObservable:LiveData<ProfileEntity>
	private lateinit var bodyObservable:LiveData<List<BodyEntity>>

	fun findProfile(owner: LifecycleOwner, observer: Observer<ProfileEntity>) {
		profileObservable=profileDao.find()
		profileObservable.observe(owner, observer)
	}

	fun findBody(owner: LifecycleOwner, observer: Observer<List<BodyEntity>>) {
		bodyObservable=bodyDao.find()
		bodyObservable.observe(owner,observer)
	}

	fun insert(date: String, weight: Int) = ioThread{
		try {
			bodyDao.insert(BodyEntity(date,weight,2000,null))
		} catch (e:SQLiteConstraintException) {
			bodyDao.updateWeight(date, weight)
		}
	}
}