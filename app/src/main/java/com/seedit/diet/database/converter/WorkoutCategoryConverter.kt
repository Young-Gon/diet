package com.seedit.diet.database.converter

import android.arch.persistence.room.TypeConverter
import com.seedit.diet.database.entity.WorkoutCategoryEnum

class WorkoutCategoryConverter {
    @TypeConverter
    fun toWorkoutCategory(index:Int)=
            WorkoutCategoryEnum.values()[index]

    @TypeConverter
    fun toIndex(category: WorkoutCategoryEnum)=
            category.ordinal
}