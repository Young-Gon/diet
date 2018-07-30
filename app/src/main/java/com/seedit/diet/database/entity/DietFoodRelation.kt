package com.seedit.diet.database.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index

@Entity(tableName = "dietfood",
		primaryKeys = ["dietId","foodId"],
		indices = [Index(value = ["dietId"]),Index(value = ["foodId"])],
		foreignKeys = [ForeignKey(entity = DietEntity::class, parentColumns = ["id"], childColumns = ["dietId"]),
						ForeignKey(entity = FoodEntity::class, parentColumns = ["_id"], childColumns = ["foodId"])])
data class DietFoodRelationEntity(
		val dietId:Long,
		val foodId:Long,
		var foodCount:Int/*,
		@PrimaryKey(autoGenerate = true)
		val id: Long=0*/)

data class DietWithFood(
		@Embedded
		var dietFood: DietFoodRelationEntity,
		@Embedded
		var food: FoodEntity
)
{
	override fun equals(other: Any?): Boolean {
		if(other==null || other !is DietWithFood)
			return false

		return other.dietFood.dietId==dietFood.dietId &&
				other.food._id==food._id
	}

	override fun hashCode(): Int {
		var result = dietFood.hashCode()
		result = 31 * result + food.hashCode()
		return result
	}
}
/*
{
	@Embedded
	lateinit var dietFood: DietFoodRelationEntity

	@Embedded
	lateinit var food: FoodEntity
}*/
