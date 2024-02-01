package com.treasurehunt.ui.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.User
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toPlaceEntity
import kotlinx.coroutines.delay

const val USER_UPDATE_DELAY = 1000L

class LoginViewModel(
    private val userRepository: UserRepository,
    private val logRepository: LogRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    suspend fun insertNaverUser(user: User) {
        updateProfile(user)
        delay(USER_UPDATE_DELAY)
        val currentUser = Firebase.auth.currentUser!!
        // 테스트 샘플
        val userDTO = UserDTO(
            email = currentUser.email,
            nickname = currentUser.displayName,
            profileImage = currentUser.photoUrl.toString()
        )
        userRepository.insert(currentUser.uid, userDTO)
    }

    suspend fun insertGuestUser() {
        val currentUser = Firebase.auth.currentUser!!
        // 테스트 샘플
        val userDTO = UserDTO(email = "")
        userRepository.insert(currentUser.uid, userDTO)
    }

    private fun updateProfile(user: User) {
        val profileUpdate = userProfileChangeRequest {
            displayName = user.nickname
            photoUri = Uri.parse(user.profileImage)
        }
        Firebase.auth.currentUser!!.updateProfile(profileUpdate)
    }

    suspend fun initLocalData() {
        val curUserUid = Firebase.auth.currentUser!!.uid
        val userDTO = userRepository.getRemoteUser(curUserUid)
        initLocalLogs(userDTO)
//            castingRemotePlaces(userDTO)
    }

    private suspend fun initLocalLogs(userDTO: UserDTO) {
        logRepository.deleteAll()
        userDTO.logs.map {
            val log = logRepository.getRemoteLog(it.key)
            logRepository.insert(log.toLogEntity(it.key))
        }
    }

    private suspend fun initLocalPlaces(userDTO: UserDTO) {
        placeRepository.deleteAll()
        userDTO.places.map {
            val place = placeRepository.getRemotePlace(it.key)
            placeRepository.insert(place.toPlaceEntity())
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return LoginViewModel(
                    TreasureHuntApplication.userRepo,
                    TreasureHuntApplication.logRepo,
                    TreasureHuntApplication.placeRepo
                ) as T
            }
        }
    }
}