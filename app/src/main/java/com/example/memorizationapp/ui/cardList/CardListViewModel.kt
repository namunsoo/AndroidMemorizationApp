package com.example.memorizationapp.ui.cardList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model

class CardListViewModel: ViewModel() {
    private val _cardBundleId = MutableLiveData<Int>()
    private val _update = MutableLiveData<CardItem>()
    private val _cardList = MutableLiveData<MutableList<CardItem>>()
    val cardBundleId: LiveData<Int> = _cardBundleId
    val cardList: LiveData<MutableList<CardItem>> = _cardList
    val update: LiveData<CardItem> = _update
    fun setValue(id : Int, cardList: MutableList<CardItem>) {
        _cardBundleId.value = id
        _cardList.value = cardList
    }

    fun setUpdate(item :CardItem) {
        _update.value = item
    }
}