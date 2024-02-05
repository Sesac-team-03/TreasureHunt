package com.treasurehunt.ui.login

import android.net.Uri
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
    private val userRepo: UserRepository,
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository
) : ViewModel() {

    suspend fun insertNaverUser(user: User) {
        updateProfile(user)
        delay(USER_UPDATE_DELAY)
        val currentUser = Firebase.auth.currentUser!!
        val userDTO = UserDTO(
            email = currentUser.email,
            nickname = currentUser.displayName,
            profileImage = currentUser.photoUrl?.toString()
        )
        userRepo.insert(currentUser.uid, userDTO)
    }

    suspend fun insertGuestUser() {
        val currentUser = Firebase.auth.currentUser!!
        val userDTO = UserDTO(
            email = ""
        )
        userRepo.insert(currentUser.uid, userDTO)
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
        val userDTO = userRepo.getRemoteUser(curUserUid)
        initLocalLogs(userDTO)
        initLocalPlaces(userDTO)
    }

    private suspend fun initLocalLogs(userDTO: UserDTO) {
        logRepo.deleteAll()
        userDTO.logs.map {
            val log = logRepo.getRemoteLog(it.key)
            logRepo.insert(log.toLogEntity(it.key))
        }
    }

    private suspend fun initLocalPlaces(userDTO: UserDTO) {
        placeRepo.deleteAll()
        userDTO.places.map {
            val place = placeRepo.getRemotePlace(it.key)
            placeRepo.insert(place.toPlaceEntity(it.key))
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