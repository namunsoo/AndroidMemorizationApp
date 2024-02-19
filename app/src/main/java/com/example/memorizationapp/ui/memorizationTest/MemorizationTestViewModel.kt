package com.example.memorizationapp.ui.memorizationTest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MemorizationTestViewModel : ViewModel() {
    var cardIdAndCardTableIdList: MutableList<MemorizationTestCard> = mutableListOf()
    var cardCenterIndex: Int? = null
    var cardIndex: Int? = null
    var position: Int? = null
}