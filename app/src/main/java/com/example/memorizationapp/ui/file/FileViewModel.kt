package com.example.memorizationapp.ui.file

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model

class FileViewModel : ViewModel() {
    private val _action = MutableLiveData<String>()
    private val _model = MutableLiveData<Model<Item>?>()
    val model: LiveData<Model<Item>?> = _model
    val action: LiveData<String> = _action
    fun setValue(model : Model<Item>?, action: String) {
        _model.value = model
        _action.value = action
    }
}