package com.treasurehunt.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO

data class LogModel(
    val place: String,
    val images: List<String>,
    val text: String,
    val theme: String,
    val createdDate: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(place)
        parcel.writeStringList(images)
        parcel.writeString(text)
        parcel.writeString(theme)
        parcel.writeLong(createdDate)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<LogModel> {
        override fun createFromParcel(parcel: Parcel): LogModel = LogModel(parcel)

        override fun newArray(size: Int): Array<LogModel?> = arrayOfNulls(size)
    }
}

fun LogModel.asLogEntity(remoteId: String? = null, localId: Long = 0): LogEntity {
    return LogEntity(place, images, text, theme, createdDate, remoteId, id = localId)
}

fun LogModel.asLogDTO(remoteId: String? = null, localId: Long = 0): LogDTO {
    return LogDTO(place, images.associateWith { true }, text, theme, createdDate)
}
