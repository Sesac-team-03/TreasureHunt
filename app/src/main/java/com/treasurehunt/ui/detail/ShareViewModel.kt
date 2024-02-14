package com.treasurehunt.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

//마커 삭제테스트 임시코드
class ShareViewModel  @Inject constructor() : ViewModel() {
    val postDeleted = MutableLiveData<String>()
}