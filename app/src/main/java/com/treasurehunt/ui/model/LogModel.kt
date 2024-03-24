package com.treasurehunt.ui.model

import android.os.Parcelable
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogModel(
    val remotePlaceId: String,
    val text: String = "",
    val theme: Int = 0,
    val createdDate: Long,
    val remoteImageIds: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val localId: Long? = null,
    val remoteId: String? = null
) : Parcelable

fun LogModel.asLogEntity(localId: Long = 0, remoteId: String? = this.remoteId) =
    LogEntity(remotePlaceId, text, theme, createdDate, remoteImageIds, localId, remoteId)

fun LogModel.asLogDTO(localId: Long = 0) =
    LogDTO(remotePlaceId, text, theme, createdDate, remoteImageIds.associateWith { true }, localId)
