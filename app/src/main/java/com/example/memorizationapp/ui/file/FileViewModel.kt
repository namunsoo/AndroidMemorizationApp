package com.example.memorizationapp.ui.file

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.model.Data

class FileViewModel : ViewModel() {
    private val _action = MutableLiveData<String>()
    private val _fileName = MutableLiveData<String>()
    private val _node = MutableLiveData<Node<Data>>()

    val action: LiveData<String> = _action
    val fileName: LiveData<String> = _fileName
    val node: LiveData<Node<Data>> = _node

    fun setValues(action : String, fileName : String, node: Node<Data> = Node(Data.File("Null"))) {
        _action.value = action
        _fileName.value = fileName
        _node.value = node
    }
}