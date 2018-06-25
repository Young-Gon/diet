package com.seedit.diet.database.converter

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?) =
        if (timestamp == null) null else Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date?) = date?.time
}