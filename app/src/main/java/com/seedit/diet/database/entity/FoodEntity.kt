package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName="food")
data class FoodEntity(
		override val name:String,
		override val calorie:Float,
		@PrimaryKey(autoGenerate = true)
		var _id: Long=0
):SearchViewInfo
{
	override fun getSubInfo()="$calorie Kcal"
}

interface SearchViewInfo {
	val name:String
	val calorie:Float
	fun getSubInfo():String
}