package com.seedit.diet.database.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class RecommendWithDiet
{

        @Embedded
        lateinit var recommendDiet: RecommendDietEntity

        @Relation(parentColumn = "id",
                entityColumn = "recommendDietId")
        lateinit var dietList:List<DietEntity>

}