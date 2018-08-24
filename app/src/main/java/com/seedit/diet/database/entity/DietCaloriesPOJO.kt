package com.seedit.diet.database.entity

import android.arch.persistence.room.ColumnInfo

data class DietCaloriesPOJO (
		@ColumnInfo(name = "calorie")
		var calorie:Float,
		@ColumnInfo(name = "createAt")
		var createAt: Float
)