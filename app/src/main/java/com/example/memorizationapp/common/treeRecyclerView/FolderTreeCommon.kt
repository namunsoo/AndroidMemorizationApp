package com.example.memorizationapp.common.treeRecyclerView

object FolderTreeCommon {
    // 트리 구조 생성
    // A폴더에 있는 B폴더, B폴더에 있는 C폴더 일 경우 [A폴더, B폴더, C폴더] 이런식으로
    fun getTargetTree(targetModel: Model<Item>): List<Model<Item>> {
        var item = targetModel
        var pathModelList = listOf<Model<Item>>()
        while (item.haveParent) {
            pathModelList = listOf(item) + pathModelList
            item = item.parent!!
        }
        pathModelList = listOf(item) + pathModelList
        return pathModelList
    }

    // 모델 추가
    fun addNewModel(
        models: MutableList<Model<Item>>,
        targetTree: List<Model<Item>>,
        treeIndex: Int,
        newModel: Model<Item>
    ): MutableList<Model<Item>>? {
        val index = models.indexOf(targetTree[treeIndex])
        if (targetTree.count() > treeIndex + 1) {
            return addNewModel(models[index].children, targetTree, treeIndex.plus(1), newModel)
        }
        val test = targetTree[treeIndex]
        val tttt = models[0]
        val qqqq = test == tttt
        models[index].addChild(newModel)
        return null
    }

    // 모델 이름 수정
    fun updateModel(
        models: MutableList<Model<Item>>,
        targetTree: List<Model<Item>>,
        treeIndex: Int,
        newFolderName: String
    ): MutableList<Model<Item>>? {
        val index = models.indexOf(targetTree[treeIndex])
        if (targetTree.count() > treeIndex + 1) {
            return updateModel(models[index].children, targetTree, treeIndex.plus(1), newFolderName)
        }
        models[index].content.name = newFolderName
        return null
    }

    // 모델 삭제
    fun deleteModel(
        models: MutableList<Model<Item>>,
        targetTree: List<Model<Item>>,
        treeIndex: Int
    ): MutableList<Model<Item>>? {
        val index = models.indexOf(targetTree[treeIndex])
        if (targetTree.count() > treeIndex + 1) {
            return deleteModel(models[index].children, targetTree, treeIndex.plus(1))
        }
        models.removeAt(index)
        return null
    }

}