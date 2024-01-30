package com.example.memorizationapp.ui.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.model.Data

class FolderViewModel : ViewModel() {
    private val _action = MutableLiveData<String>()
    private val _folderName = MutableLiveData<String>()
    private val _node = MutableLiveData<Node<Data>>()

    val action: LiveData<String> = _action
    val folderName: LiveData<String> = _folderName
    val node: LiveData<Node<Data>> = _node

    fun setValues(action : String, folderName : String, node: Node<Data> = Node(Data.Directory("Null"))) {
        _action.value = action
        _folderName.value = folderName
        _node.value = node
    }
}