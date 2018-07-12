package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "recommendWorkoutRelationship",
		foreignKeys = [ForeignKey(
			entity = RecommendWorkoutEntity::class,
				parentColumns = ["id"],
				childColumns = ["recommendId"],
				onDelete = ForeignKey.CASCADE)],
		indices = [Index(value = ["recommendId"])])
class RecommendWorkoutRelationshipEntity (
	val recommendId:Long,
	val recommendDay: Date = Date(),
	@PrimaryKey(autoGenerate = true)
	val id: Long=0
)