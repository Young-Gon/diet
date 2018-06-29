package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import java.util.*

@Entity(tableName = "diet",
        foreignKeys = [ForeignKey(
                entity = RecommendDietEntity::class,
                parentColumns = ["id"],
                childColumns = ["recommendDietId"],
                onDelete = ForeignKey.CASCADE
        )],
        indices = [(Index(value = ["recommendDietId"]))])
data class DietEntity(
        val recommendDietId:Long=0,
        val category: DietCategoryEnum,
        val content: String,
        val picture: Uri?=null,
        val createAt: Date=Date(),
        @PrimaryKey(autoGenerate = true)
        val id: Long=0)

enum class DietCategoryEnum(val title:String) {
    BREAKFIRST("아침"),
    LAUNCH("점심"),
    DINER("저녁"),
    SNACK("간식"),
    NIGHT_SNACK("야식"),
}