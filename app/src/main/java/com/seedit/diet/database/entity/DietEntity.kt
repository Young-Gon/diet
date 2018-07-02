package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "diet")
data class DietEntity(
		@PrimaryKey(autoGenerate = true)
		val id: Long,
		var category: DietCategoryEnum=DietCategoryEnum.BREAKFAST,
		var content: String="",
		var picture: Uri?=null,
		val createAt: Date=Date()) : Parcelable {

	constructor(ids: Long=0):this(id=ids)

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			DietCategoryEnum.values()[parcel.readInt()],
			parcel.readString(),
			parcel.readParcelable(Uri::class.java.classLoader),
			Date(parcel.readLong()))

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(category.ordinal)
		parcel.writeString(content)
		parcel.writeParcelable(picture, flags)
		parcel.writeLong(createAt.time)
		parcel.writeLong(id)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<DietEntity> {
		override fun createFromParcel(parcel: Parcel): DietEntity {
			return DietEntity(parcel)
		}

		override fun newArray(size: Int): Array<DietEntity?> {
			return arrayOfNulls(size)
		}
	}
}

enum class DietCategoryEnum(val title:String) {
	BREAKFAST("아침"),
	LAUNCH("점심"),
	DINER("저녁"),
	SNACK("간식"),
	NIGHT_SNACK("야식"),
}