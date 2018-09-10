package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.DietFoodRelationEntity
import com.seedit.diet.database.entity.DietWithFood

@Dao
interface DietFoodRelationDao
{
	@Query("SELECT * FROM dietfood, food WHERE  food._id=dietfood.foodId AND dietfood.dietId=:dietId")
	fun findByDietID(dietId:Long):LiveData<List<DietWithFood>>

	@Insert(onConflict = OnConflictStrategy.FAIL)
	fun insertAll(dietFoodRelationEntity: List<DietFoodRelationEntity>)

	//@Update(onConflict = OnConflictStrategy.REPLACE)
	@Query("UPDATE dietfood SET foodCount=foodCount+1 WHERE dietId=:dietId AND foodId=:foodId")
	fun update(dietId:Long,foodId:Long)

	@Update
	fun update(dietFoodRelationEntity: DietFoodRelationEntity)

	@Query("DELETE FROM dietfood")
	fun deleteAll()
}