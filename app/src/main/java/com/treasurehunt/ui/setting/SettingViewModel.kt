package com.treasurehunt.ui.setting

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.LoginRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toPlaceEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val placeRepo: PlaceRepository,
    private val logRepo: LogRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository,
    private val loginRepo: LoginRepository
) : ViewModel() {

    suspend fun deleteAllData(userId: String) {
        val userDTO = userRepo.getRemoteUserById(userId)
        userDTO.remoteLogIds.filterValues { it }.keys.forEach { logId ->
            deleteLogImages(
                logId,
                userId
            )
        }
        deleteProfileImages(userId)
        userDTO.remoteLogIds.filterValues { it }.keys.forEach { logId -> deleteLog(logId) }
        userDTO.remotePlanIds.filterValues { it }.keys.forEach { planId -> deletePlan(planId) }
        userDTO.remoteVisitIds.filterValues { it }.keys.forEach { visitId -> deleteVisit(visitId) }
        deleteUser(userId)
    }

    private suspend fun deleteLogImages(logId: String, userId: String) {
        val remoteLog = logRepo.getRemoteLogById(logId)
        val storageRef = Firebase.storage.reference.child("${userId}/log_images")
        remoteLog.remoteImageIds.filterValues { it }.keys.forEach { id ->
            val imageFileName = imageRepo.getRemoteImageById(id).url.substringAfter("log_images/")
            storageRef.child("/$imageFileName").delete()
            imageRepo.delete(id)
        }
    }

    private fun deleteProfileImages(userId: String) {
        val folderRef = Firebase.storage.reference.child("${userId}/profile_image")
        folderRef.listAll().addOnSuccessListener { result ->
            result.items.forEach { item ->
                item.delete()
            }
        }
    }

    private suspend fun deleteLog(logId: String) {
        val remoteLog = logRepo.getRemoteLogById(logId)
        val localLog = remoteLog.toLogEntity(logId)
        logRepo.delete(localLog)
        logRepo.delete(logId)
    }

    private suspend fun deletePlan(planId: String) {
        val plan = placeRepo.getRemotePlaceById(planId).toPlaceEntity(planId)
        placeRepo.delete(plan)
        placeRepo.delete(planId)
    }

    private suspend fun deleteVisit(visitId: String) {
        val visit = placeRepo.getRemotePlaceById(visitId).toPlaceEntity(visitId)
        placeRepo.delete(visit)
        placeRepo.delete(visitId)
    }

    private suspend fun deleteUser(userId: String) {
        userRepo.delete(userId)
    }

    suspend fun getSwitchState(): Flow<Boolean> {
        return loginRepo.getAutoLoginState
    }

    suspend fun updateSwitchState(isChecked: Boolean) {
        loginRepo.updateAutoLoginSwitch(isChecked)
    }
}