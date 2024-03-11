package com.treasurehunt.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.UserPreferencesRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.ui.model.NaverUser
import com.treasurehunt.util.FILENAME_EXTENSION_PNG
import com.treasurehunt.util.STORAGE_LOCATION_PROFILE_IMAGE
import com.treasurehunt.util.extractDigits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val loginRepo: UserPreferencesRepository
) : ViewModel() {

    suspend fun insertNaverUser(naverUser: NaverUser, currentUser: FirebaseUser) {
        val profileImageStorageUrl = uploadProfileImage(naverUser.profileImage, currentUser)
        val user = UserDTO(
            email = naverUser.email,
            nickname = naverUser.nickname,
            profileImage = profileImageStorageUrl
        )
        userRepo.insert(currentUser.uid, user)
    }

    private suspend fun uploadProfileImage(
        profileImageUrl: String?,
        currentUser: FirebaseUser
    ): String? {
        if (profileImageUrl == null) return null

        val filename = profileImageUrl.extractDigits()
        val profileImageStorageRef =
            Firebase.storage.reference.child(currentUser.uid).child(STORAGE_LOCATION_PROFILE_IMAGE)
                .child("$filename$FILENAME_EXTENSION_PNG")
        val input = withContext(Dispatchers.IO) {
            URL(profileImageUrl).openStream()
        }
        val uploadTask = profileImageStorageRef.putStream(input)
        uploadTask.await()

        return if (uploadTask.isSuccessful) {
            uploadTask.snapshot.storage.toString()
        } else {
            null
        }
    }

    suspend fun insertGuestUser(uid: String) {
        val user = UserDTO(
            email = ""
        )
        userRepo.insert(uid, user)
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

    fun getSwitchState(): Flow<Boolean> {
        return loginRepo.getAutoLoginState
    }
}