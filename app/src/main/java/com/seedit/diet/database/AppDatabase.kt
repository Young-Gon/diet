package com.seedit.diet.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.gondev.clog.CLog
import com.seedit.diet.R
import com.seedit.diet.database.converter.DateConverter
import com.seedit.diet.database.converter.DietCategoryConverter
import com.seedit.diet.database.converter.UriConverter
import com.seedit.diet.database.dao.DietDao
import com.seedit.diet.database.dao.ProfileDao
import com.seedit.diet.database.dao.RecommendDietDao
import com.seedit.diet.database.entity.DietCategoryEnum
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.database.entity.RecommendDietEntity
import com.seedit.diet.util.ioThread
import java.util.*

@Database(entities = arrayOf(ProfileEntity::class, RecommendDietEntity::class,DietEntity::class), version = 1)
@TypeConverters(DateConverter::class, UriConverter::class, DietCategoryConverter::class)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun profileDao(): ProfileDao

    abstract fun recommendDietDao(): RecommendDietDao

    abstract fun dietDao(): DietDao

    companion object
    {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "com.seedit.diet.db")
                        .addCallback(object : RoomDatabase.Callback()
                        {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                //TODO 초기 디비 값 설정
                                CLog.i("디비 생성")
                                ioThread {
                                    val calendar= Calendar.getInstance()
                                    calendar.add(Calendar.DATE,-1)

                                    AppDatabase.getInstance(context.applicationContext).apply {
                                        insertData(this,getRecommendDietList(),getDietList(calendar))
                                    }
                                }
                            }
                        })
                        .build()

        fun getRecommendDietList()=arrayOf(
                RecommendDietEntity(R.drawable.recommend1,"오트밀 20g, 바나나 100g, 사과 125g, 우유 200ml",345),
                RecommendDietEntity(R.drawable.recommend2,"현미밥 70g, 미역국 300g, 배추김치 50g, 가자미구이 50g",207),
                RecommendDietEntity(R.drawable.recommend3,"삶은 달걀 100g, 찐 고구마 200g, 닭가슴살 샐러드 100g",448)
        )

        fun getDietList(calendar: Calendar) =arrayOf(
                DietEntity(1, DietCategoryEnum.BREAKFIRST,"어제 아침",null,calendar.time),
                DietEntity(1, DietCategoryEnum.LAUNCH,"어제 점심",null,calendar.time),
                DietEntity(1, DietCategoryEnum.DINER,"어제 저녁",null,calendar.time),
                DietEntity(2, DietCategoryEnum.BREAKFIRST,"오늘 아침",null),
                DietEntity(2, DietCategoryEnum.LAUNCH,"오늘 점심",null),
                DietEntity(2, DietCategoryEnum.DINER,"오늘 저녁",null)
        )

        private fun insertData(database: AppDatabase, recommendDietList: Array<RecommendDietEntity>, dietList: Array<DietEntity>) =
        database.runInTransaction{
            database.recommendDietDao().insertAll(recommendDietList)
            database.dietDao().insertAll(dietList)
        }
    }
}