package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.RecommendDietEntity

@Dao
interface RecommendDietDao
{
    @Query("SELECT * FROM recommend_diet")
    fun findAll(): LiveData<List<RecommendDietEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profile: Array<RecommendDietEntity>)

    @Query("SELECT * FROM recommend_diet ORDER BY RANDOM() LIMIT 1")
    fun findOneByRandom(): RecommendDietEntity

    /*@Query("SELECT recommend_diet.* FROM recommend_diet JOIN diet ON diet.recommendDietId=recommend_diet.id  WHERE diet.recommendDay BETWEEN :from AND :to GROUP BY recommend_diet.id")
    fun findJoin(from: Date, to: Date):LiveData<List<RecommendWithDiet>>*/
}