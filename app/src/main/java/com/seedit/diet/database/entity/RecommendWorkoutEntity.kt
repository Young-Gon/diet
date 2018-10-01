package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "recommend_workout")
data class RecommendWorkoutEntity(
		override val name:String,
		val content:String,
		override val calorie:Float,
		val imageRes: String?,
		@PrimaryKey(autoGenerate = true)
		val id: Long=0
):SearchViewInfo {
	override fun getSubInfo()="$calorie Kcal/30분"
}