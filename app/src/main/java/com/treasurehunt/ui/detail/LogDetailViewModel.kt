package com.treasurehunt.ui.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.PlaceService
import com.treasurehunt.data.remote.UserService
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.toLogModel
import com.treasurehunt.ui.model.LogModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val logRepository: LogRepository,
    private val userRepository: UserRepository,
    private val imageRepo: ImageRepository,
    private val shareViewModel: ShareViewModel,
    private val dispatcher: CoroutineDispatcher,
    private val placeService: PlaceService,
    private val logService: LogService,
    private val userService: UserService

) : ViewModel() {

    //    val logDeleted = MutableLiveData<String>()
//    val placeDeleted = MutableLiveData<String>()
    suspend fun getPlace(placeId: String): LogModel? {
        val placeDTO: PlaceDTO = placeRepository.getRemotePlace(placeId)
        val logId = placeDTO.log

        return if (logId != null) {
            val logDTO: LogDTO = logRepository.getRemoteLog(logId)
            logDTO.toLogModel(imageRepo)
        } else {
            null
        }
    }

    suspend fun getRemotePlace(placeId: String): PlaceDTO {
        return placeRepository.getRemotePlace(placeId)
    }

//

//    fun deletePost(placeId: String, userId: String, logId: String) {
//        // Place 삭제
//        viewModelScope.launch {
//            try {
//                val deletePlaceResponse = placeService.deletePlace(placeId)
//                if (!deletePlaceResponse.isSuccessful) {
//                    Log.e("DeletePost", "Failed to delete place: ${deletePlaceResponse.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DeletePost", "Exception while deleting place: ${e.message}")
//            }
//        }
//
//        // Log 삭제
//        viewModelScope.launch {
//            try {
//                val deleteLogResponse = logService.deleteLog(logId)
//                if (!deleteLogResponse.isSuccessful) {
//                    Log.e("DeletePost", "Failed to delete log: ${deleteLogResponse.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DeletePost", "Exception while deleting log: ${e.message}")
//            }
//        }
//
//        // User 삭제
//        viewModelScope.launch {
//            try {
//                val deleteUserResponse = userService.deleteUser(userId)
//                if (!deleteUserResponse.isSuccessful) {
//                    Log.e("DeletePost", "Failed to delete user: ${deleteUserResponse.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DeletePost", "Exception while deleting user: ${e.message}")
//            }
//        }
//    }

//    fun deletePost(placeId: String, userId: String, logId: String) {
//        viewModelScope.launch {
//            try {
//                val deletePlaceResponse = placeService.deletePlace(placeId)
//                val deleteLogResponse = logService.deleteLog(logId)
//                val deleteUserResponse = userService.deleteUser(userId)
//
//                if (!deletePlaceResponse.isSuccessful || !deleteLogResponse.isSuccessful || !deleteUserResponse.isSuccessful) {
//                    Log.e("DeletePost", "Failed to delete all data: Place - ${deletePlaceResponse.errorBody()?.string()}, Log - ${deleteLogResponse.errorBody()?.string()}, User - ${deleteUserResponse.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DeletePost", "Exception while deleting data: ${e.message}")
//            }
//        }
//    }

    fun deletePost(placeId: String, userId: String, logId: String) {
        viewModelScope.launch {

            val deleteLogJob = async {
                val deleteLogResponse = logService.deleteLog(logId)
                if (!deleteLogResponse.isSuccessful) {
                    Log.e(
                        "DeletePost",
                        "Failed to delete log: ${deleteLogResponse.errorBody()?.string()}"
                    )
                }
                logRepository.deleteAll()
                delay(1000)
            }

            val deleteUserJob = async {
                val deleteUserResponse = userService.deleteUser(userId)
                if (!deleteUserResponse.isSuccessful) {
                    Log.e(
                        "DeletePost",
                        "Failed to delete user: ${deleteUserResponse.errorBody()?.string()}"
                    )
                }
            }

            val deletePlaceJob = async {
                val deletePlaceResponse = placeService.deletePlace(placeId)
                if (!deletePlaceResponse.isSuccessful) {
                    Log.e(
                        "DeletePost",
                        "Failed to delete place: ${deletePlaceResponse.errorBody()?.string()}"
                    )
                }
                placeRepository.deleteAll()
            }
            deleteLogJob.await()
            deletePlaceJob.await()
            deleteUserJob.await()
        }
    }

//    fun deletePost(placeId: String, userId: String, logId: String) {
//        viewModelScope.launch {
//            // User의 places와 logs 삭제
////            FirebaseDatabase.getInstance().getReference("/users/$userId/places/$placeId").removeValue()
////            FirebaseDatabase.getInstance().getReference("/users/$userId/logs/$logId").removeValue()
//            // User의 places와 logs를 false로 설정
////            FirebaseDatabase.getInstance().getReference("/users/$userId/places/$placeId").setValue(false)
////            FirebaseDatabase.getInstance().getReference("/users/$userId/logs/$logId").setValue(false)
//            val updates = hashMapOf<String, Any?>(
//                "/users/$userId/places/$placeId" to null,
//                "/users/$userId/logs/$logId" to null
//            )
//            userService.deleteUser2(userId, updates)
//
//            val deletePlaceJob = async {
//                val deletePlaceResponse = placeService.deletePlace(placeId)
//                if (!deletePlaceResponse.isSuccessful) {
//                    Log.e("DeletePost", "Failed to delete place: ${deletePlaceResponse.errorBody()?.string()}")
//                }
//                delay(1000)
//            }
//
//            val deleteLogJob = async {
//                val deleteLogResponse = logService.deleteLog(logId)
//                if (!deleteLogResponse.isSuccessful) {
//                    Log.e("DeletePost", "Failed to delete log: ${deleteLogResponse.errorBody()?.string()}")
//                }
//                delay(1000)
//            }
//
//            // 모든 작업이 완료될 때까지 기다립니다.
//            deletePlaceJob.await()
//            deleteLogJob.await()
//        }
//    }

    //실행안됨
//    fun deletePost(placeId: String, userId: String, logId: String) {
//        viewModelScope.launch {
//            // User의 places와 logs 삭제
//            val updates = UserService.UserUpdates(
//                places = null,
//                logs = null
//            )
//            userService.deleteUser2(userId, updates)
//
//            val deletePlaceJob = async {
//                val deletePlaceResponse = placeService.deletePlace(placeId)
//                if (!deletePlaceResponse.isSuccessful) {
//                    Log.e(
//                        "DeletePost",
//                        "Failed to delete place: ${deletePlaceResponse.errorBody()?.string()}"
//                    )
//                }
//                delay(1000)
//            }
//
//            val deleteLogJob = async {
//                val deleteLogResponse = logService.deleteLog(logId)
//                if (!deleteLogResponse.isSuccessful) {
//                    Log.e(
//                        "DeletePost",
//                        "Failed to delete log: ${deleteLogResponse.errorBody()?.string()}"
//                    )
//                }
//                delay(1000)
//            }
//
//            // 모든 작업이 완료될 때까지 기다립니다.
//            deletePlaceJob.await()
//            deleteLogJob.await()
//        }
//    }


//    fun markPostAsDeleted(placeId: String, userId: String) {
//        viewModelScope.launch(dispatcher) {
//            try {
//                // Place "삭제된 것처럼" 처리
//                placeRepository.markPlaceAsDeleted(placeId)
//
//                // Log "삭제된 것처럼" 처리를 위해 먼저 Log ID를 가져옵니다.
//                val placeDTO = placeRepository.getRemotePlace(placeId)
//                placeDTO.log?.let { logId ->
//                    logRepository.markLogAsDeleted(logId)
//
//                    // User에서 Place와 Log "삭제된 것처럼" 처리
//                    userRepository.markUserPlaceAndLogAsDeleted(userId, placeId, logId)
//                }
//
//                // 처리 완료 후에 UI 업데이트나 다른 후속 조치를 취할 수 있습니다.
//                // 예를 들어, LiveData를 업데이트하거나, 성공 메시지를 표시할 수 있습니다.
//            } catch (e: Exception) {
//                // 오류 처리
//                // 예를 들어, 오류 메시지를 로그에 기록하거나, 사용자에게 알릴 수 있습니다.
//            }
//        }
//    }
}