package com.seedit.diet.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.FoodEntity

@Dao
interface FoodDao
{
	@Query("SELECT * FROM food where name like :keyword")
	fun find(keyword:String): List<FoodEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(foodEntity: Array<FoodEntity>): List<Long>
}