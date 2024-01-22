package com.treasurehunt.ui.login

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.profile.data.NidProfile
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.local.LogRepository
import com.treasurehunt.data.local.PlaceRepository
import com.treasurehunt.data.remote.FirebaseRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toPlaceEntity
import kotlinx.coroutines.delay

const val USER_UPDATE_DELAY = 1000L

class LoginViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val logRepository: LogRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    suspend fun resisterUser(naverProfile: NidProfile) {
        updateProfile(naverProfile)
        delay(USER_UPDATE_DELAY)
        val user = Firebase.auth.currentUser!!
        val userDTO = UserDTO(
            email = user.email!!,
            nickname = user.displayName,
            profileImage = user.photoUrl.toString(),
            logs = listOf("-NoV18iQHObalOr66Yh4", "0"),
            places = listOf("0")
        )
        firebaseRepository.resisterUser(user.uid, userDTO)
    }

    suspend fun castingRemoteData() {
        val curUserUid = Firebase.auth.currentUser!!.uid
        val userDTO = firebaseRepository.getUserData(curUserUid)
        castingRemoteLogs(userDTO)
//            castingRemotePlaces(userDTO)
    }

    private suspend fun castingRemoteLogs(userDTO: UserDTO) {
        logRepository.deleteAll()
        userDTO.logs.map {
            val log = firebaseRepository.getLog(it)
            logRepository.insert(log.toLogEntity())
        }
    }

    private suspend fun castingRemotePlaces(userDTO: UserDTO) {
        placeRepository.deleteAll()
        userDTO.places.map {
            val place = firebaseRepository.getPlace(it)
            placeRepository.insert(place.toPlaceEntity())
        }
    }

    private fun updateProfile(naverProfile: NidProfile) {
        val profileUpdate = userProfileChangeRequest {
            displayName = naverProfile.nickname
            photoUri = Uri.parse(naverProfile.profileImage)
        }
        Firebase.auth.currentUser!!.updateProfile(profileUpdate)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return LoginViewModel(
                    TreasureHuntApplication.firebaseRepository,
                    TreasureHuntApplication.logRepo,
                    TreasureHuntApplication.placeRepo
                ) as T
            }
        }
    }
}