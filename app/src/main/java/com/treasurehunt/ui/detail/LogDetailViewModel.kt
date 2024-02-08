package com.treasurehunt.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.treasurehunt.ui.model.LogModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Dagger 모듈 생성
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }
}
@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val database: DatabaseReference
) : ViewModel() {

    private val _logModel = MutableStateFlow<LogModel?>(null)
    val logModel: StateFlow<LogModel?> = _logModel.asStateFlow()

    fun getLogDataFromFirebase(logId: String) {
        viewModelScope.launch {
            val reference = database.child("logs").child(logId)
            val dataSnapshot = reference.get().await()
            if (dataSnapshot.exists()) {
                val createdDate = dataSnapshot.child("createdDate").getValue(Long::class.java)?.toString() ?: ""
                val images = dataSnapshot.child("images").children.map { it.getValue(String::class.java) ?: "" }
                val place = dataSnapshot.child("place").getValue(String::class.java) ?: ""
                val text = dataSnapshot.child("text").getValue(String::class.java) ?: ""
                val theme = dataSnapshot.child("theme").getValue(String::class.java)?.toLong() ?: 0L
                _logModel.value = LogModel(createdDate, images, place, text, theme)
            } else {
                _logModel.value = null
            }
        }
    }
}
