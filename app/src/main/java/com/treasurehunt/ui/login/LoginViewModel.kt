package com.treasurehunt.ui.login

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.ui.model.NaverUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository
) : ViewModel() {

    suspend fun insertNaverUser(naverUser: NaverUser, currentUser: FirebaseUser) {
        updateProfile(naverUser, currentUser)

        val user = UserDTO(
            email = naverUser.email,
            nickname = naverUser.nickname,
            profileImage = naverUser.profileImage
        )
        userRepo.insert(currentUser.uid, user)
    }

    suspend fun insertGuestUser(uid: String) {
        val user = UserDTO(
            email = ""
        )
        userRepo.insert(uid, user)
    }

    private fun updateProfile(naverUser: NaverUser, currentUser: FirebaseUser) {
        val profileUpdate = userProfileChangeRequest {
            displayName = naverUser.nickname
            photoUri = Uri.parse(naverUser.profileImage)
        }
        currentUser.updateProfile(profileUpdate)
    }

    suspend fun initLocalData(uid: String) {
        val user = userRepo.getRemoteUserById(uid)
        initLocalLogs(user)
        initLocalPlaces(user)
    }

    private suspend fun initLocalLogs(user: UserDTO) {
        logRepo.deleteAllLocalLogs()
        user.remoteLogIds.filterValues { it }.map {
            val log = logRepo.getRemoteLogById(it.key)
            logRepo.insert(log.toLogEntity(it.key))
        }
    }

    private suspend fun initLocalPlaces(user: UserDTO) {
        placeRepo.deleteAllLocalPlaces()
        (user.remoteVisitIds + user.remotePlanIds).filterValues { it }.map {
            val place = placeRepo.getRemotePlaceById(it.key)
            placeRepo.insert(place.toPlaceEntity(it.key))
        }
    }

    suspend fun getProfileImageStorageUrl(uid: String): String? {
        val user = userRepo.getRemoteUserById(uid)
        return user.profileImage
    }
}