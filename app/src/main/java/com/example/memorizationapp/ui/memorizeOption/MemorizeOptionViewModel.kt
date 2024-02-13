package com.example.memorizationapp.ui.memorizeOption

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model

class MemorizeOptionViewModel : ViewModel() {
    // private val _checkedRows: MutableList<Int> = mutableListOf()
    //    private val _tempCheckedRows: MutableList<Int> = mutableListOf()
    //    private val _cardTableId: MutableList<Int> = mutableListOf()
    //    private val _tempCardTableId: MutableList<Int> = mutableListOf()
    //    private var _cardType: String = ALL
    //    private var _cardSequence: String = RANDOM
    private val _cardTableIdList = MutableLiveData<MutableList<Int>>(mutableListOf())
    private val _cardType = MutableLiveData<String>()
    private val _cardSequence = MutableLiveData<String>()

    val cardTableIdList: LiveData<MutableList<Int>> = _cardTableIdList
    val cardType: LiveData<String> = _cardType
    val cardSequence: LiveData<String> = _cardSequence
    fun setValue(cardTableIdList : MutableList<Int>, cardType: String, cardSequence: String) {
        _cardTableIdList.value = cardTableIdList
        _cardType.value = cardType
        _cardSequence.value = cardSequence
    }
}