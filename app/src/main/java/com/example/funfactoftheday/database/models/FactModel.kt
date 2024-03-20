package com.example.funfactoftheday.database.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fact_table")
data class FactModel (
    @PrimaryKey(autoGenerate = false)
    val factName: String,
    val isFavorite: Boolean = false,
    var isDeletable: Boolean = false
): Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ){
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(factName)
        parcel.writeByte(if(isFavorite) 1 else 0)
        parcel.writeByte(if(isDeletable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<FactModel>{
        override fun createFromParcel(parcel: Parcel?): FactModel {
            return FactModel(parcel!!)
        }

        override fun newArray(size: Int): Array<FactModel?> {
            return arrayOfNulls(size)
        }
    }
}

inline fun <reified T: Parcelable> MutableList<T>.toParcelableArray(): Array<T>{
    return this.toTypedArray()
}