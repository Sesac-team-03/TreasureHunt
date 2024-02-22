package com.treasurehunt.ui.model

import android.os.Parcelable
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogModel(
    val place: String,
    val text: String,
    val theme: String,
    val createdDate: Long,
    val imageIds: List<String>,
    val imageUrls: List<String> = emptyList(),
) : Parcelable

fun LogModel.asLogEntity(remoteId: String? = null, localId: Long = 0): LogEntity {
    val (place, text, theme, createdDate, imageIds) = this
    return LogEntity(place, text, theme, createdDate, imageIds, localId, remoteId)
}

fun LogModel.asLogDTO(localId: Long = 0): LogDTO {
    val (place, text, theme, createdDate,imageIds) = this
    return LogDTO(place, text, theme, createdDate, imageIds.associateWith { true }, localId)
}