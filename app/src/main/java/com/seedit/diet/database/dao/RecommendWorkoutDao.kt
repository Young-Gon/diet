package com.seedit.diet.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.RecommendWorkoutEntity

@Dao
interface RecommendWorkoutDao
{
	@Query("SELECT * FROM recommend_workout")
	fun findAll(): List<RecommendWorkoutEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(profile: Array<RecommendWorkoutEntity>)

	@Query("SELECT * FROM recommend_workout ORDER BY RANDOM() LIMIT 1")
	fun findOneByRandom(): RecommendWorkoutEntity

	@Query("SELECT * FROM recommend_workout where name like :keyword")
	fun find(keyword: String): List<RecommendWorkoutEntity>
}