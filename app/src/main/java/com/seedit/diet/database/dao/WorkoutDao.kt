package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.DietCaloriesPOJO
import com.seedit.diet.database.entity.WorkoutEntity
import java.util.*

@Dao
interface WorkoutDao
{
	@Query("SELECT * FROM workout ")
	fun findAll():LiveData<List<WorkoutEntity>>

	@Query("SELECT workout.* FROM workout WHERE date(workout.createAt/1000,'unixepoch')=date(:date/1000,'unixepoch')")
	fun findByDate(date: Date): LiveData<List<WorkoutEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(workEntity: WorkoutEntity):Long

	@Delete
	fun delete(workEntity: WorkoutEntity)

	@Query("SELECT sum(calorie) FROM workout WHERE date(createAt/1000,'unixepoch')=date(:date/1000,'unixepoch')")
	fun findTotalCalories(date: Date?): LiveData<Float>

	@Query("SELECT sum(calorie) as calorie, createAt FROM workout GROUP BY date(createAt/1000,'unixepoch') ORDER BY createAt ASC")
	fun findCaloriesGroupByDate(): LiveData<List<DietCaloriesPOJO>>

	@Query("""SELECT sum(calorie) AS calorie, createAt FROM (
			SELECT diet.calorie AS calorie, diet.createAt AS createAt FROM diet
			UNION
			SELECT -workout.calorie AS calorie, workout.createAt AS createAt FROM workout)
			GROUP BY date(createAt/1000,'unixepoch')
			ORDER BY createAt ASC""")
	fun findCalorieDietWorkout(): LiveData<List<DietCaloriesPOJO>>
}