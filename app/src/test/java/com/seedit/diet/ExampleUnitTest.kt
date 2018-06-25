package com.seedit.diet

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        bmiTest(12f)
        bmiTest(17.7f)
        bmiTest(22f)
        bmiTest(26f)
        bmiTest(32f)
        bmiTest(38f)
    }

    fun bmiTest(bmi: Float) {
        assertEquals(getProgress(bmi,0f,18.5f), (bmi*20/18.5).toInt())
        assertEquals(getProgress(bmi,18.5f,23f), ((bmi-18.5)*20/4.5).toInt())
        assertEquals(getProgress(bmi,23f,25f), ((bmi-23)*20/2).toInt())
        assertEquals(getProgress(bmi,25f,30f), ((bmi-25)*20/5).toInt())
        assertEquals(getProgress(bmi,30f,40f), ((bmi-30)*20/10).toInt())
    }

    fun getProgress(bmi:Float,min:Float,max:Float): Int {
        return ((bmi-min)*20/(max-min)).toInt()
    }
}
