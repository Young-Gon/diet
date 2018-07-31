package com.seedit.diet.viewmodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.seedit.diet.database.AppDatabase

class ViewModelFactory(private val application: Application, private val database:AppDatabase)
	: ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>)=
            modelClass.constructors.first().newInstance(application,database) as T
}

fun <T : ViewModel> FragmentActivity.getViewModel(modelClass: Class<T>)=
    ViewModelProviders.of(this,ViewModelFactory(application, AppDatabase.getInstance(this))).get(modelClass)

fun <T : ViewModel> Fragment.getViewModel(modelClass: Class<T>)=
	ViewModelProviders.of(this,ViewModelFactory(activity?.application!!, AppDatabase.getInstance(context!!))).get(modelClass)
