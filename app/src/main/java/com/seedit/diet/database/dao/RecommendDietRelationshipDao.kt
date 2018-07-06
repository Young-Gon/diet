package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.RecommendDietEntity
import com.seedit.diet.database.entity.RecommendDietRelationshipEntity
import java.util.*

@Dao
interface RecommendDietRelationshipDao
{
	@Query("SELECT * FROM recommend_diet JOIN recommendDietRelationship ON recommend_diet.id=recommendDietRelationship.recommendId WHERE recommendDietRelationship.recommendDay BETWEEN :from AND :to GROUP BY recommend_diet.id")
    fun find(from: Date, to: Date): LiveData<List<RecommendDietEntity>>

	@Query("SELECT * FROM recommendDietRelationship")
	fun findAll(): LiveData<List<RecommendDietRelationshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: RecommendDietRelationshipEntity)
}