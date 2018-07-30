package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.WorkoutEntity
import java.util.*

@Dao
interface WorkoutDao
{
	@Query("SELECT workout.* FROM workout WHERE date(workout.createAt/1000,'unixepoch')=date(:date/1000,'unixepoch')")
	fun findByDate(date: Date): LiveData<List<WorkoutEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(workEntity: WorkoutEntity):Long

	@Delete
	fun delete(workEntity: WorkoutEntity)
}