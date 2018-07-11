package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.DietEntity
import java.util.*

@Dao
interface DietDao {
    @Query("SELECT * FROM diet ")
    fun findAll():LiveData<List<DietEntity>>

    @Query("SELECT * FROM diet WHERE diet.createAt BETWEEN :from AND :to")
    fun findByDate(from: Date, to:Date):LiveData<List<DietEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(diet: Array<DietEntity>):List<Long>

	@Delete
	fun delete(dietEntity: DietEntity)
}