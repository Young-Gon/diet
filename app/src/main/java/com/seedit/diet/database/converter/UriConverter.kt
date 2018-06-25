package com.seedit.diet.database.converter

import android.arch.persistence.room.TypeConverter
import android.net.Uri

class UriConverter {
    @TypeConverter
    fun toUri(path:String?)=
            if(path==null) null else Uri.parse(path)

    @TypeConverter
    fun toPath(uri: Uri?)=uri?.toString()
}