package com.treasurehunt.ui.model

import android.os.Parcelable
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogModel(
    val place: String,
    val imageIds: List<String>,
    val imageUrls: List<String> = emptyList(),
    val text: String,
    val theme: String,
    val createdDate: Long
):Parcelable

fun LogModel.asLogEntity(remoteId: String? = null, localId: Long = 0): LogEntity {
    val (place, imageIds, _, text, theme, createdDate) = this
    return LogEntity(place, imageIds, text, theme, createdDate, remoteId, localId)
}

fun LogModel.asLogDTO(localId: Long = 0): LogDTO {
    val (place, imageIds, _, text, theme, createdDate) = this
    return LogDTO(place, imageIds.associateWith { true }, text, theme, createdDate, localId)
}