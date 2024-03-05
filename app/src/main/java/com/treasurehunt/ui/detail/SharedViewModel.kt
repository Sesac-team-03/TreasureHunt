package com.treasurehunt.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.treasurehunt.ui.model.LogModel

//마커 삭제테스트 임시코드
class SharedViewModel : ViewModel() {
    val selectedLogModel = MutableLiveData<LogModel>()

    fun selectLogModel(logModel: LogModel) {
        selectedLogModel.value = logModel
    }
}