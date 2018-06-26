package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import java.util.*

@Entity(tableName = "profile")
data class ProfileEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        var profile_image: Uri?=null,
        var name: String="",
        var gender:Int=0,
        var birthday: Date=Date(0),
        var weight:Int=0,
        var height: Int=0,
        var targetWeight:Int=0,
        var targetWorkout:Int=0,
        var targetDiet:Int=0,
        var targetWater:Int=0,
        var targetDday:Date=Date(0)
        )