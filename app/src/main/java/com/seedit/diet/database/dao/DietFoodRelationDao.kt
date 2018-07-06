package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.DietFoodRelationEntity
import com.seedit.diet.database.entity.DietWithFood

@Dao
interface DietFoodRelationDao
{
	@Query("SELECT * FROM dietfood, food WHERE  food._id=dietfood.foodId AND dietfood.dietId=:dietId")
	fun findByDietID(dietId:Long):LiveData<List<DietWithFood>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(dietFoodRelationEntity: Array<DietFoodRelationEntity>)
}