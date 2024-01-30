package com.example.memorizationapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.model.Data

class MainViewModel : ViewModel() {
    var nodes = mutableListOf<Node<Data>>()
}