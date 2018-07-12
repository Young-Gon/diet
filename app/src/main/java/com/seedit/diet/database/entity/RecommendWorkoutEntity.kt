package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "recommend_workout")
data class RecommendWorkoutEntity(
		val name:String,
		val content:String,
		val imageRes: Int,
		@PrimaryKey(autoGenerate = true)
		val id: Long=0
)