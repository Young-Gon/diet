package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.seedit.diet.database.entity.BodyEntity
import io.reactivex.Flowable

@Dao
interface BodyDao {
	@Query("SELECT * FROM body order by date ASC")
	fun find(): LiveData<List<BodyEntity>>

	@Query("UPDATE body SET weight=:weight WHERE date=:date")
	fun updateWeight(date: String, weight: Int)

	@Query("UPDATE body SET water=:water WHERE date=:date")
	fun updateWater(date: String, water: Int)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(bodyEntity: BodyEntity)

	@Query("SELECT * FROM body order by date ASC")
	fun findRx(): Flowable<List<BodyEntity>>

	@Query("DELETE FROM body")
	fun deleteAll()
}