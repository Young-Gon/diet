package com.seedit.diet.database.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index

@Entity(tableName = "workoutRelationship",
		primaryKeys = ["workoutID","recommendWorkoutID"],
		foreignKeys = [ForeignKey(entity = WorkoutEntity::class,parentColumns = ["id"],childColumns = ["workoutID"],onDelete = ForeignKey.CASCADE),
						ForeignKey(entity = RecommendWorkoutEntity::class, parentColumns = ["id"], childColumns = ["recommendWorkoutID"])],
		indices = [Index(value = ["workoutID"]),Index(value = ["recommendWorkoutID"])])
data class WorkoutRelationshipEntity (
		var workoutID:Long,
		val recommendWorkoutID:Long,
		var time:Int=30
)

data class WorkoutWithRecommend(
		@Embedded
		var relationship:WorkoutRelationshipEntity,
		@Embedded
		var recommendWorkoutEntity: RecommendWorkoutEntity
)
{
	override fun equals(other: Any?): Boolean {
		if(other==null || other !is WorkoutWithRecommend)
			return false

		return other.relationship.workoutID==relationship.workoutID &&
				other.relationship.recommendWorkoutID==relationship.recommendWorkoutID
	}

	override fun hashCode(): Int {
		var result = relationship.hashCode()
		result = 31 * result + recommendWorkoutEntity.hashCode()
		return result
	}
}
/*
{
	@Embedded
	lateinit var relationship:WorkoutRelationshipEntity

	@Embedded
	lateinit var recommendWorkoutEntity: RecommendWorkoutEntity
}*/
