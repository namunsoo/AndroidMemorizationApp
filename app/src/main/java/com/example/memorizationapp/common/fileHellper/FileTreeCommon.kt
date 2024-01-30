package com.example.memorizationapp.common.fileHellper

import com.example.memorizationapp.model.Data
import org.json.JSONArray
import org.json.JSONObject

object FileTreeCommon {
    // 트리 구조 생성
    // A폴더에 있는 B폴더, B폴더에 있는 C폴더 일 경우 [A폴더, B폴더, C폴더] 이런식으로
    fun getTargetTree(targetNode: Node<Data>): List<Node<Data>> {
        var item = targetNode
        var pathNodeList = listOf<Node<Data>>()
        while (!item.isRoot) {
            pathNodeList = listOf(item) + pathNodeList
            item = item.parent!!
        }
        pathNodeList = listOf(item) + pathNodeList
        return pathNodeList
    }

    // 노드 추가
    fun addNewNode(
        nodes: MutableList<Node<Data>>,
        targetTree: List<Node<Data>>,
        treeIndex: Int,
        newNode: Node<Data>
    ): MutableList<Node<Data>>? {
        val index = nodes.indexOf(targetTree[treeIndex])
        if (targetTree.count() > treeIndex + 1) {
            return addNewNode(nodes[index].children, targetTree, treeIndex.plus(1), newNode)
        }
        nodes[index].addChild(newNode)
        return null
    }

    // 노드 이름 수정
    fun updateNode(
        nodes: MutableList<Node<Data>>,
        targetTree: List<Node<Data>>,
        treeIndex: Int,
        newFolderName: String
    ): MutableList<Node<Data>>? {
        val index = nodes.indexOf(targetTree[treeIndex])
        if (targetTree.count() > treeIndex + 1) {
            return updateNode(nodes[index].children, targetTree, treeIndex.plus(1), newFolderName)
        }
        nodes[index].content.name = newFolderName
        return null
    }

    // 노드 삭제
    fun deleteNode(
        nodes: MutableList<Node<Data>>,
        targetTree: List<Node<Data>>,
        treeIndex: Int
    ): MutableList<Node<Data>>? {
        val index = nodes.indexOf(targetTree[treeIndex])
        if (targetTree.count() > treeIndex + 1) {
            return deleteNode(nodes[index].children, targetTree, treeIndex.plus(1))
        }
        nodes.removeAt(index)
        return null
    }

    fun getTargetJson(targetNode: Node<Data>): List<String> {
        var item = targetNode
        var nameList = listOf<String>()
        while (!item.isRoot) {
            nameList = listOf(item.content.name) + nameList
            item = item.parent!!
        }
        nameList = listOf(item.content.name) + nameList
        return nameList
    }

    fun createJsonObject(
        data: JSONArray,
        nameList: List<String>,
        index: Int,
        newObject: JSONObject
    ): JSONObject? {
        val jsonArray = findJsonObjectByName(data, nameList[index]).getJSONArray("children")
        if (nameList.count() > index + 1) {
            return createJsonObject(jsonArray, nameList, index.plus(1), newObject)
        }
        jsonArray.put(newObject)
        return null
    }

    fun updateJsonObject(
        data: JSONArray,
        nameList: List<String>,
        index: Int,
        newName: String
    ): JSONObject? {
        val jsonObject = findJsonObjectByName(data, nameList[index])
        if (nameList.count() > index + 1) {
            return updateJsonObject(
                jsonObject.getJSONArray("children"),
                nameList,
                index.plus(1),
                newName
            )
        }
        jsonObject.put("name", newName)
        return null
    }

    fun deleteJsonObject(data: JSONArray, nameList: List<String>, index: Int): JSONObject? {
        val jsonObject = findJsonObjectByName(data, nameList[index])
        if (nameList.count() > index + 1) {
            return deleteJsonObject(jsonObject.getJSONArray("children"), nameList, index.plus(1))
        }

        var idx: Int = 0
        for (i in 0 until data.length()) {
            if (data.getJSONObject(i).optString("name").equals(jsonObject.optString("name"))) {
                idx = i
            }
        }
        data.remove(idx)
        return null
    }

    fun findJsonObjectByName(jsonArray: JSONArray, name: String): JSONObject {
        var jsonObject = JSONObject()
        var result = JSONObject()
        for (i in 0 until jsonArray.length()) {
            jsonObject = jsonArray.getJSONObject(i)
            if (jsonObject.optString("name").equals(name)) {
                result = jsonObject
            }
        }
        return result
    }

    fun checkIsSameName(list: MutableList<Node<Data>>, name: String): Boolean {
        for (item in list) {
            if (name == item.content.name) {
                return true
            }
        }
        return false
    }
}