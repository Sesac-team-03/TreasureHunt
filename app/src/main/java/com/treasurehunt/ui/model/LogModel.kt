package com.treasurehunt.ui.model
import android.os.Parcelable
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.android.parcel.Parcelize
@Parcelize
data class LogModel(
    val remotePlaceId: String,
    val text: String,
    val theme: String,
    val createdDate: Long,
    val remoteImageIds: List<String>,
    val imageUrls: List<String> = emptyList(),
    val localId: Long? = null,
    val remoteId: String? = null
) : Parcelable

fun LogModel.asLogEntity(localId: Long = 0, remoteId: String? = null) =
    LogEntity(remotePlaceId, text, theme, createdDate, remoteImageIds, this.localId?:0, remoteId)

fun LogModel.asLogDTO(localId: Long = 0) =
    LogDTO(remotePlaceId, text, theme, createdDate, remoteImageIds.associateWith { true }, localId)
