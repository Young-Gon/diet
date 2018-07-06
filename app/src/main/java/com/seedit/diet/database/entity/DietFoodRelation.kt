package com.seedit.diet.database.entity

import android.arch.persistence.room.*

@Entity(tableName = "dietfood",
		indices = [Index(value = ["dietId"]),Index(value = ["foodId"])],
		foreignKeys = [ForeignKey(entity = DietEntity::class, parentColumns = ["id"], childColumns = ["dietId"]),
						ForeignKey(entity = FoodEntity::class, parentColumns = ["_id"], childColumns = ["foodId"])])
data class DietFoodRelationEntity(
		val dietId:Long,
		val foodId:Long,
		var foodCount:Int,
		@PrimaryKey(autoGenerate = true)
		val id: Long=0)

class DietWithFood {
	@Embedded
	lateinit var dietFood: DietFoodRelationEntity

	@Embedded
	lateinit var food: FoodEntity
}