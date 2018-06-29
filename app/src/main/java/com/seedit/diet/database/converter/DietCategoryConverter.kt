package com.seedit.diet.database.converter

import android.arch.persistence.room.TypeConverter
import com.seedit.diet.database.entity.DietCategoryEnum

class DietCategoryConverter {
    @TypeConverter
    fun toDietCategory(index:Int)=
            DietCategoryEnum.values()[index]

    @TypeConverter
    fun toIndex(category: DietCategoryEnum)=
            category.ordinal
}