package com.example.memorizationapp.ui.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {
    private val _cardBundleId = MutableLiveData<Int>()
    private val _cardQuestion = MutableLiveData<String?>()
    private val _cardAnswer = MutableLiveData<String?>()
    private val _cardMemorized = MutableLiveData<Int?>()
    private val _action = MutableLiveData<String>()

    val cardBundleId: LiveData<Int> = _cardBundleId
    val cardQuestion: LiveData<String?> = _cardQuestion
    val cardAnswer: LiveData<String?> = _cardAnswer
    val cardMemorized: LiveData<Int?> = _cardMemorized
    val action: LiveData<String> = _action
    fun setValue(id : Int, cardQuestion : String?, cardAnswer : String?, cardMemorized : Int?, action : String) {
        _cardBundleId.value = id
        _cardQuestion.value = cardQuestion
        _cardAnswer.value = cardAnswer
        _cardMemorized.value = cardMemorized
        _action.value = action
    }
}