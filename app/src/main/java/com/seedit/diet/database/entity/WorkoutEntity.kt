package com.seedit.diet.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "workout")
data class WorkoutEntity (
		@PrimaryKey(autoGenerate = true)
		var id: Long,
		var category: WorkoutCategoryEnum=WorkoutCategoryEnum.AEROBIC,
		var content: String="",
		var calorie:Float=0f,
		var picture: Uri?=null,
		val createAt: Date = Date()
) : Parcelable
{
	constructor(ids: Long=0):this(id=ids)

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			WorkoutCategoryEnum.values()[parcel.readInt()],
			parcel.readString(),
			parcel.readFloat(),
			parcel.readParcelable(Uri::class.java.classLoader),
			Date(parcel.readLong()))

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeInt(category.ordinal)
		parcel.writeString(content)
		parcel.writeFloat(calorie)
		parcel.writeParcelable(picture, flags)
		parcel.writeLong(createAt.time)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<WorkoutEntity> {
		override fun createFromParcel(parcel: Parcel): WorkoutEntity {
			return WorkoutEntity(parcel)
		}

		override fun newArray(size: Int): Array<WorkoutEntity?> {
			return arrayOfNulls(size)
		}
	}
}

enum class WorkoutCategoryEnum(val title: String){
	AEROBIC("유산소"),
	ANAEROBIC("무산소");

	override fun toString(): String {
		return title
	}
}

fun convertWorkCategoryToString()=WorkoutCategoryEnum.values().map {
	it.title
}