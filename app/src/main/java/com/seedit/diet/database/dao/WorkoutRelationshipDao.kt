package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.WorkoutRelationshipEntity
import com.seedit.diet.database.entity.WorkoutWithRecommend

@Dao
interface WorkoutRelationshipDao
{
	@Query("SELECT workoutRelationship.*,recommend_workout.* FROM workoutRelationship,recommend_workout where workoutRelationship.recommendWorkoutID=recommend_workout.id AND workoutRelationship.workoutID=:id")
	fun find(id: Long): LiveData<List<WorkoutWithRecommend>>

	@Delete
	fun delete(relationship: WorkoutRelationshipEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(workoutRelationshipList: List<WorkoutRelationshipEntity>)
}