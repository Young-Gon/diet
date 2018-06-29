package com.seedit.diet.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
object AppExecutors
{
    val diskIO:Executor=Executors.newSingleThreadExecutor()
    val networkIO:Executor= Executors.newFixedThreadPool(3)
    val mainThread=object : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    AppExecutors.diskIO.execute(f)
}

fun mainThread(f: () -> Unit) {
    AppExecutors.mainThread.execute(f)
}