package com.example.memorizationapp.common.fileHellper

import com.example.memorizationapp.model.Data

object FileTreeCommon {
    // 트리 구조 생성
    // A폴더에 있는 B폴더, B폴더에 있는 C폴더 일 경우 [A폴더, B폴더, C폴더] 이런식으로
    fun getTargetTree(targetNode: Node<Data>): List<Node<Data>> {
        var item = targetNode
        var pathNodeList = listOf<Node<Data>>()
        while (!item.isRoot){
            pathNodeList = listOf(item) + pathNodeList
            item = item.parent!!
        }
        pathNodeList = listOf(item) + pathNodeList
        return pathNodeList
    }

    // 노드 추가
    fun addNewNode(nodes : MutableList<Node<Data>>, targetTree : List<Node<Data>>, treeIndex : Int, newNode: Node<Data>): MutableList<Node<Data>>? {
        val index = nodes.indexOf(targetTree[treeIndex])
        if(targetTree.count() > treeIndex + 1){
            return addNewNode(nodes[index].children, targetTree, treeIndex.plus(1), newNode)
        }
        nodes[index].addChild(newNode)
        return null
    }

    // 노드 이름 수정
    fun updateNode(nodes : MutableList<Node<Data>>, targetTree : List<Node<Data>>, treeIndex : Int, newFolderName : String): MutableList<Node<Data>>? {
        val index = nodes.indexOf(targetTree[treeIndex])
        if(targetTree.count() > treeIndex + 1){
            return updateNode(nodes[index].children, targetTree, treeIndex.plus(1), newFolderName)
        }
        nodes[index].content.name = newFolderName
        return null
    }

    // 노드 삭제
    fun deleteNode(nodes : MutableList<Node<Data>>, targetTree : List<Node<Data>>, treeIndex : Int): MutableList<Node<Data>>? {
        val index = nodes.indexOf(targetTree[treeIndex])
        if(targetTree.count() > treeIndex + 1){
            return deleteNode(nodes[index].children, targetTree, treeIndex.plus(1))
        }
        nodes.removeAt(index)
        return null
    }
}