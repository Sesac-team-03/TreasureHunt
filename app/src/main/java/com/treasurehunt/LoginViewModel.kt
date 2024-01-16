package com.treasurehunt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private var _auth: MutableLiveData<FirebaseAuth?> = MutableLiveData(null)
    val auth: LiveData<FirebaseAuth> get() = _auth as LiveData<FirebaseAuth>

    init {
        _auth.value = Firebase.auth
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return LoginViewModel() as T
            }
        }
    }
}