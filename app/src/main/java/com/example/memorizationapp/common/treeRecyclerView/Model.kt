package com.example.memorizationapp.common.treeRecyclerView

class Model<T>(val content: T) {
    var parent: Model<T>? = null

    private val _children = mutableListOf<Model<T>>()
    val children: MutableList<Model<T>>
        get() = _children

    var isOpen = false

    val haveParent: Boolean
        get() = parent != null

    val haveChildren: Boolean
        get() = children.isNotEmpty()

    private var _depth = UNDEFINE
    val depth: Int
        get() {
            if (haveParent)
                _depth = parent!!.depth + 1
            else
                _depth = 0
            return _depth
        }

    fun addChild(child: Model<T>): Model<T> {
        _children.add(child)
        child.parent = this

        return this
    }

    fun toggle() {
        isOpen = !isOpen
    }

    companion object {
        private const val UNDEFINE = -1
    }
}
