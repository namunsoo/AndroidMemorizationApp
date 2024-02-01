package com.example.memorizationapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model

class MainViewModel : ViewModel() {
    private val _modelList = MutableLiveData<MutableList<Model<Item>>>(mutableListOf())
    val modelList: LiveData<MutableList<Model<Item>>> = _modelList
    fun setValue(modelList : MutableList<Model<Item>>) {
        _modelList.value = modelList
    }
}