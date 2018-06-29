package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.RecommendDietEntity
import com.seedit.diet.database.entity.RecommendWithDiet
import java.util.*

@Dao
interface RecommendDietDao
{
    @Query("SELECT * FROM recommend_diet")
    fun findAll(): LiveData<List<RecommendDietEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profile: Array<RecommendDietEntity>)

    @Query("SELECT recommend_diet.* FROM recommend_diet JOIN diet ON diet.recommendDietId=recommend_diet.id  WHERE diet.createAt BETWEEN :from AND :to GROUP BY recommend_diet.id")
    fun findJoin(from: Date, to: Date):LiveData<List<RecommendWithDiet>>
    /*@Query("SELECT * FROM recommend_diet ")
    fun findJoin():LiveData<List<RecommendWithDiet>>*/
}