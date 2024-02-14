package com.treasurehunt.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ShareViewModel  @Inject constructor() : ViewModel() {
    val postDeleted = MutableLiveData<String>()
}