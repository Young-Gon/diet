package com.seedit.diet

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.matcher.ViewMatchers.assertThat
import android.support.test.runner.AndroidJUnit4
import com.gondev.clog.CLog
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.database.dao.*
import com.seedit.diet.database.entity.DietCategoryEnum
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.DietFoodRelationEntity
import com.seedit.diet.database.entity.RecommendDietRelationshipEntity
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
	private lateinit var recommendDietRelationshipDao: RecommendDietRelationshipDao
    private lateinit var dietFoodRelationDao: DietFoodRelationDao
	private lateinit var foodDao: FoodDao

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
	    dietFoodRelationDao=mDatabase.dietFoodDao()
	    foodDao=mDatabase.foodDao()
	    recommendDietRelationshipDao=mDatabase.recommendDietRelationshipDao()
    }

    @Test
    fun useAppContext() {
        /*recommendDietDao.insertAll(AppDatabase.getRecommendDietList())
	    dietDao.insertAll(getDietList())
	    foodDao.insertAll(AppDatabase.getFoodList())
	    dietFoodRelationDao.insertAll(getDietFootList())

	    val diet = LiveDataTestUtil.getValue(dietFoodRelationDao.findByDietID(1))

	    diet.forEach {
	    }

        assertThat(diet.size, `is`(3))*/
	    recommendDietDao.insertAll(AppDatabase.getRecommendDietList())
	    recommendDietDao.findAll().let{
		    recommendDietRelationshipDao.insert(RecommendDietRelationshipEntity(it.shuffled()[0].id))
	    }
	    val diet = LiveDataTestUtil.getValue(recommendDietRelationshipDao.findAll())

	    CLog.d("size=${diet.size}")
	    diet.forEach {
	    }
	    assertThat(diet.size, `is`(3))
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        mDatabase.close()
    }

    companion object
    {
        fun getDietList()=arrayOf(
                DietEntity(1,DietCategoryEnum.BREAKFAST,"오트밀 20g, 바나나 100g, 사과 125g, 우유 200ml"),
                DietEntity(2,DietCategoryEnum.LAUNCH,"현미밥 70g, 미역국 300g, 배추김치 50g, 가자미구이 50g"),
                DietEntity(3,DietCategoryEnum.DINER,"삶은 달걀 100g, 찐 고구마 200g, 닭가슴살 샐러드 100g")
        )

	    fun getDietFootList()= arrayOf(
			    DietFoodRelationEntity(1,1,1),
			    DietFoodRelationEntity(1,2,2),
			    DietFoodRelationEntity(1,3,3),
			    DietFoodRelationEntity(2,4,4)
	    )
    }
}
