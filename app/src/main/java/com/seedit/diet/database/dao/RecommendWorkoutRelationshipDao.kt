package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.database.entity.RecommendWorkoutRelationshipEntity
import java.util.*

@Dao
interface RecommendWorkoutRelationshipDao {

	@Query("SELECT recommend_workout.* FROM recommend_workout,recommendWorkoutRelationship WHERE recommend_workout.id=recommendWorkoutRelationship.recommendId AND date(recommendWorkoutRelationship.recommendDay/1000,'unixepoch')=date(:date/1000,'unixepoch')")
	fun find(date: Date):LiveData<List<RecommendWorkoutEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(recommendWorkoutRelationshipEntity: RecommendWorkoutRelationshipEntity)

	@Query("DELETE FROM recommendWorkoutRelationship")
	fun deleteAll()
}