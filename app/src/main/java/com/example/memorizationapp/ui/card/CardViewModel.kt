package com.example.memorizationapp.ui.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {
    private val _cardListRow = MutableLiveData<Int>().apply { value = 0 }
    private val _cardId = MutableLiveData<Int>()
    private val _cardBundleId = MutableLiveData<Int>()
    private val _cardQuestion = MutableLiveData<String?>()
    private val _cardAnswer = MutableLiveData<String?>()
    private val _cardMemorized = MutableLiveData<Int?>()
    private val _action = MutableLiveData<String>()

    val cardListRow: LiveData<Int> = _cardListRow
    val cardId: LiveData<Int> = _cardId
    val cardBundleId: LiveData<Int> = _cardBundleId
    val cardQuestion: LiveData<String?> = _cardQuestion
    val cardAnswer: LiveData<String?> = _cardAnswer
    val cardMemorized: LiveData<Int?> = _cardMemorized
    val action: LiveData<String> = _action
    fun setValue(row: Int, id : Int, bundleId : Int, cardQuestion : String?, cardAnswer : String?, cardMemorized : Int?, action : String) {
        _cardListRow.value = row
        _cardId.value = id
        _cardBundleId.value = bundleId
        _cardQuestion.value = cardQuestion
        _cardAnswer.value = cardAnswer
        _cardMemorized.value = cardMemorized
        _action.value = action
    }
    fun setRow(row: Int) {
        _cardListRow.value = row
    }

}