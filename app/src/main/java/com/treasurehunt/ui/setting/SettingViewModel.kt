package com.treasurehunt.ui.setting

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserPreferencesRepository
import com.treasurehunt.data.UserRepository
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
    private val loginRepo: UserPreferencesRepository
) : ViewModel() {

    suspend fun deleteAllUserData(userId: String) {
        val userDTO = userRepo.getRemoteUserById(userId)
        userDTO.remoteLogIds.filterValues { it }.keys.forEach { logId ->
            deleteLogImages(logId, userId)
            deleteLog(logId)
        }
        logRepo.deleteAllLocalLogs()
        deleteProfileImage(userId)
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

    private suspend fun deleteProfileImage(userId: String) {
        val remoteUser = userRepo.getRemoteUserById(userId)
        val storageRef = Firebase.storage.reference.child("${userId}/profile_image")
        val profileImage = remoteUser.profileImage?.substringAfter("profile_image/")
        storageRef.child("/$profileImage").delete()
    }

    private suspend fun deleteLog(logId: String) {
        logRepo.delete(logId)
    }

    private suspend fun deletePlan(planId: String) {
        val localPlan = placeRepo.getRemotePlaceById(planId).toPlaceEntity(planId)
        placeRepo.delete(localPlan)
        placeRepo.delete(planId)
    }

    private suspend fun deleteVisit(visitId: String) {
        val localVisit = placeRepo.getRemotePlaceById(visitId).toPlaceEntity(visitId)
        placeRepo.delete(localVisit)
        placeRepo.delete(visitId)
    }

    private suspend fun deleteUser(userId: String) {
        userRepo.delete(userId)
    }

    fun getSwitchState(): Flow<Boolean> {
        return loginRepo.getAutoLoginState
    }

    suspend fun updateSwitchState(isChecked: Boolean) {
        loginRepo.updateAutoLoginSwitch(isChecked)
    }
}