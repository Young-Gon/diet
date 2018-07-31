package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.DietEntity
import java.util.*

@Dao
interface DietDao {
    @Query("SELECT * FROM diet ")
    fun findAll():LiveData<List<DietEntity>>

    @Query("SELECT diet.* FROM diet WHERE date(diet.createAt/1000,'unixepoch')=date(:date/1000,'unixepoch')")
    fun findByDate(date: Date):LiveData<List<DietEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(diet: DietEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(diet: Array<DietEntity>):List<Long>

	@Delete
	fun delete(dietEntity: DietEntity)

	@Query("SELECT * FROM diet WHERE date(diet.createAt/1000,'unixepoch')=date(:date/1000,'unixepoch')")
	fun select(date: Date):LiveData<List<DietEntity>>
}