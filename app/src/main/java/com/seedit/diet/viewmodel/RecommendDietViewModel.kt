package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import com.gondev.clog.CLog
import com.seedit.diet.database.dao.RecommendDietDao
import com.seedit.diet.database.entity.RecommendDietEntity
import com.seedit.diet.database.entity.RecommendWithDiet
import java.util.*

class RecommendDietViewModel(application: Application, private val recommendDietDao: RecommendDietDao,calendar: Calendar) : AndroidViewModel(application)
{
    // set by default null, until we get data from the database.observable
    var observable: LiveData<List<RecommendWithDiet>> = find(calendar)

    private val mediatorLiveData = MediatorLiveData<List<RecommendWithDiet>>().apply {
        addSource(observable) { value = it }
    }

    private fun find(calendar: Calendar): LiveData<List<RecommendWithDiet>> {
        val newCal= calendar.clone() as Calendar
        newCal.set(Calendar.HOUR_OF_DAY,0)
        newCal.set(Calendar.MINUTE,0)
        newCal.set(Calendar.SECOND,0)
        val to= newCal.time
        newCal.add(Calendar.DATE,1)
        val from=newCal.time

        CLog.d("to=${to}, from=${from}")
        //observable.postValue(recommendDietDao.findJoin(to,from).value)
        return recommendDietDao.findJoin(to,from)
    }

    fun observe(owner: LifecycleOwner , observer: Observer<List<RecommendWithDiet>> ) {
        mediatorLiveData.observe(owner, observer)
    }

    fun findByDate(calendar: Calendar) {
        mediatorLiveData.removeSource(observable)
        observable  = find(calendar)
        mediatorLiveData.addSource(observable) { mediatorLiveData.value = it }
    }

    fun createNewData() = RecommendWithDiet().apply {
        recommendDiet= findRecommendDiet().value?.shuffled()?.get(0)!!
        dietList= mutableListOf()
    }

    fun findRecommendDiet(): LiveData<List<RecommendDietEntity>> {
        return recommendDietDao.findAll()
    }

    class Factory(val application: Application,val  dao: RecommendDietDao,val calendar: Calendar) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.constructors.first().newInstance(application,dao,calendar) as T
        }
    }
}