package com.siang.wei.mybookmark.db.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EpisodePages (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "pageUrl")  val pageUrl: String,
    @ColumnInfo(name = "content") val content: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(pageUrl)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EpisodePages> {
        override fun createFromParcel(parcel: Parcel): EpisodePages {
            return EpisodePages(parcel)
        }

        override fun newArray(size: Int): Array<EpisodePages?> {
            return arrayOfNulls(size)
        }
    }


}
