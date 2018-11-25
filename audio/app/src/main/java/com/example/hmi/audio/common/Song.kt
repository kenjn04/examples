package com.example.hmi.audio.common

import android.content.res.AssetFileDescriptor
import android.os.Parcel
import android.os.Parcelable

data class Song(val track: String, val fileDescriptor: AssetFileDescriptor) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(AssetFileDescriptor::class.java.classLoader)) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(track)
        parcel.writeParcelable(fileDescriptor, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }

}