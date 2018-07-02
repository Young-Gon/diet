package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName="food")
data class FoodEntity(
		val name:String,
		val calorie:Float,
		@PrimaryKey(autoGenerate = true)
		val _id: Long=0
)