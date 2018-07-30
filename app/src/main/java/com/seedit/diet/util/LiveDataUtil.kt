package com.seedit.diet.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(observer: (T?)->Unit) {
	observeForever(object : Observer<T> {
		override fun onChanged(t: T?) {
			//observer.onChanged(t)
			observer(t)
			removeObserver(this)
		}
	})
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?)->Unit) {
	observe(owner, Observer<T> { t ->
		//observer.onChanged(t)
		observer(t)
	})
}