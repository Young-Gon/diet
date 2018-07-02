package com.seedit.diet.database.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class RecommendWithDiet
{
        @Embedded
        lateinit var recommendDiet: RecommendDietEntity

        @Relation(parentColumn = "id",
                entityColumn = "recommendId")
        lateinit var dietList:List<RecommendDietRelationshipEntity>
}