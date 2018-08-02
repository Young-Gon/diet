package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri

@Entity(tableName = "body")
data class BodyEntity(
		@PrimaryKey
		var date: String,
		var weight: Int,
		var water: Int,
		var image: Uri?
)
