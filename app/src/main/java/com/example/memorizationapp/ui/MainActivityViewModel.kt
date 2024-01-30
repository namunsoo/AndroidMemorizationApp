package com.example.memorizationapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class MainActivityViewModel : ViewModel() {
    private val _fileTreeJson  = MutableLiveData<JSONObject>()
    val fileTreeJson: LiveData<JSONObject> get() = _fileTreeJson

    fun setFileTreeJson(data: JSONObject) {
        _fileTreeJson.value = data
    }
}