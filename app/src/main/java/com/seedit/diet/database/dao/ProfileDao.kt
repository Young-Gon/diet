package com.seedit.diet.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.seedit.diet.database.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile")
    fun findAll(): LiveData<List<ProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: ProfileEntity)

    @Update
    fun update(profile: ProfileEntity)

    @Delete
    fun delete(profile: ProfileEntity)
}