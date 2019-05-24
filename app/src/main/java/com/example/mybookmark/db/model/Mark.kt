package com.example.mybookmark.db.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class Mark(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "name") var name: String? = "",
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "image") var image: String? = "",
    @ColumnInfo(name = "readEpisode") var readEpisode: String? = "", //（後面
    @ColumnInfo(name = "totalEpisode") var totalEpisode: String? = "",
    @ColumnInfo(name = "updateDate") var updateDate: String = "19000101", //更新時間
    @ColumnInfo(name = "lastTimeDate") var lastTimeDate: String = "18990101", //上次閱讀時間 （後面
    @ColumnInfo(name = "comicType") var comicType: Int? = null,
    @ColumnInfo(name = "type") var type: String? = "",
    @ColumnInfo(name = "isReaded") var isReaded: Boolean? = true

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(image)
        parcel.writeString(readEpisode)
        parcel.writeString(totalEpisode)
        parcel.writeString(updateDate)
        parcel.writeString(lastTimeDate)
        parcel.writeValue(comicType)
        parcel.writeString(type)
        parcel.writeValue(isReaded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mark> {
        override fun createFromParcel(parcel: Parcel): Mark {
            return Mark(parcel)
        }

        override fun newArray(size: Int): Array<Mark?> {
            return arrayOfNulls(size)
        }
    }
}