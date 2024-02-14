package com.treasurehunt.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.treasurehunt.ui.model.LogModel

class SharedViewModel : ViewModel() {
    val selectedLogModel = MutableLiveData<LogModel>()

    fun selectLogModel(logModel: LogModel) {
        selectedLogModel.value = logModel
    }
}