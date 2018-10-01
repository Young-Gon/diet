package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "recommend_diet")
data class RecommendDietEntity(
        val dietImageRes: String,
        val dietContent:String,
        val dietCalory:Int,
        @PrimaryKey(autoGenerate = true)
        val id: Long=0
)
