package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class ViewModelFactory<DAO>(private val application: Application,private val dao:DAO) : ViewModelProvider.Factory  {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.constructors.first().newInstance(application,dao) as T
    }
}