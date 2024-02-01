package com.example.memorizationapp.ui.cardList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model

class CardListViewModel: ViewModel() {
    private val _cardBundleId = MutableLiveData<Int?>()
    private val _cardList = MutableLiveData<MutableList<String>?>()
    val cardBundleId: LiveData<Int?> = _cardBundleId
    val cardList: LiveData<MutableList<String>?> = _cardList
    fun setValue(id : Int?, cardList: MutableList<String>) {
        _cardBundleId.value = id
        _cardList.value = cardList
    }
}