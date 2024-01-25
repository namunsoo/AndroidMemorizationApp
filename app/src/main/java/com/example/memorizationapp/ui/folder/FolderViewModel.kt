package com.example.memorizationapp.ui.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorizationapp.common.Node
import com.example.memorizationapp.model.Data

class FolderViewModel : ViewModel() {
    private val _folderPath = MutableLiveData<String>()
    private val _action = MutableLiveData<String>()
    private val _folderName = MutableLiveData<String>()
    private val _node = MutableLiveData<Node<Data>?>()
    private val _position = MutableLiveData<Int?>()

    val folderPath: LiveData<String> = _folderPath
    val action: LiveData<String> = _action
    val folderName: LiveData<String> = _folderName
    val node: LiveData<Node<Data>?> = _node
    val position: LiveData<Int?> = _position

    fun setValues(folderPath : String, action : String, folderName : String, node: Node<Data>?, position: Int?) {
        _folderPath.value = folderPath
        _action.value = action
        _folderName.value = folderName
        _node.value = node
        _position.value = position
    }
}