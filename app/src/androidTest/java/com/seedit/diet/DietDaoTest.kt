package com.seedit.diet

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.matcher.ViewMatchers.assertThat
import android.support.test.runner.AndroidJUnit4
import com.gondev.clog.CLog
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.dao.DietDao
import com.seedit.diet.database.dao.RecommendDietDao
import com.seedit.diet.database.entity.DietCategoryEnum
import com.seedit.diet.database.entity.DietEntity
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DietDaoTest
{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mDatabase: AppDatabase

    private lateinit var dietDao: DietDao

    private lateinit var recommendDietDao: RecommendDietDao

    @Before
    @Throws(Exception::class)
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build()

        dietDao = mDatabase.dietDao()
        recommendDietDao=mDatabase.recommendDietDao()
    }

    @Test
    fun useAppContext() {
        recommendDietDao.insertAll(AppDatabase.getRecommendDietList())
        dietDao.insertAll(getDietList())

        val recommendDiet = LiveDataTestUtil.getValue(recommendDietDao.findAll())
        val calendar= Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.set(Calendar.SECOND,0)
        calendar.add(Calendar.DATE,-11)
        val to= calendar.time
        calendar.add(Calendar.DATE,1)
        val from=calendar.time
        CLog.d("to=${to}, from=${from}")
        val diet = LiveDataTestUtil.getValue(recommendDietDao.findJoin(to,from))
        //val diet = LiveDataTestUtil.getValue(dietDao.findAll())
        CLog.d("RecommendWithDiet.size=${diet.size}")
        diet.forEach {
            CLog.d("recommendDiet=${it.recommendDiet.toString()},\ndietList=${it.dietList.size}")
        }

        /*val recommendWithDiet = LiveDataTestUtil.getValue(recommendDietDao.findJoin())
        CLog.d("RecommendWithDiet.size=${recommendWithDiet.size}")
        recommendWithDiet.forEach {
            CLog.d("recommendDiet=${it.recommendDiet.toString()},\ndietList=${it.dietList}")
        }*/

        assertThat(diet.size, `is`(0))
        /*assertThat<Int>(recommendDiet.size, `is`(AppDatabase.getRecommendDietList().size))
        assertThat<Int>(diet.size, `is`(getDietList().size))*/
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        mDatabase.close()
    }

    companion object
    {
        fun getDietList()=arrayOf(
                DietEntity(1, DietCategoryEnum.BREAKFIRST,"오트밀 20g, 바나나 100g, 사과 125g, 우유 200ml",null),
                DietEntity(1,DietCategoryEnum.LAUNCH,"현미밥 70g, 미역국 300g, 배추김치 50g, 가자미구이 50g",null),
                DietEntity(1,DietCategoryEnum.DINER,"삶은 달걀 100g, 찐 고구마 200g, 닭가슴살 샐러드 100g",null)
        )
    }
}
