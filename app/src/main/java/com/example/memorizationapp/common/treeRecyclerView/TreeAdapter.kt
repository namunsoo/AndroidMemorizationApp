package com.example.memorizationapp.common.treeRecyclerView

import androidx.recyclerview.widget.RecyclerView

abstract class TreeAdapter<T, VH: TreeViewHolder<T>>(
    models: List<Model<T>> = emptyList()
) : RecyclerView.Adapter<VH>() {

    protected val displayItems = mutableListOf<Model<T>>()

    init {
        setDisplayItems(models)
    }

    override fun getItemCount() = displayItems.size

    private fun setDisplayItems(models: List<Model<T>>) {
        models.forEach { model ->
            displayItems.add(model)
            if (model.haveChildren && model.isOpen) {
                setDisplayItems(model.children)
            }
        }
    }

    open fun toggle(model: Model<T>): Boolean {
        if (!model.haveChildren) return false

        val isOpen = model.isOpen
        val startPosition = displayItems.indexOf(model) + 1

        if (isOpen)
            notifyItemRangeRemoved(startPosition, removeChild(model, true))
        else
            notifyItemRangeInserted(startPosition, addChild(model, startPosition))
        model.isOpen = !isOpen
        return !isOpen
    }

    fun replace(model: Model<T>) {
        displayItems.remove(model)
        notifyDataSetChanged()
    }

    fun replaceAll(models: List<Model<T>>) {
        displayItems.clear()
        setDisplayItems(models)
        notifyDataSetChanged()
    }

    private fun addChild(parent: Model<T>, startIndex: Int): Int {
        val childList = parent.children
        var addChildCount = 0

        childList.forEach { child ->
            displayItems.add(startIndex + addChildCount++, child)
            if (child.isOpen) {
                addChildCount += addChild(child, startIndex + addChildCount)
            }
        }

        if (!parent.isOpen) parent.toggle()

        return addChildCount
    }

    private fun removeChild(parent: Model<T>, shouldToggle: Boolean = true): Int {
        if (!parent.haveChildren) return 0

        val childList = parent.children
        var removeChildCount = childList.size
        displayItems.removeAll(childList)

        childList.forEach { child ->
            if (child.isOpen) {
                child.toggle()
                removeChildCount += removeChild(child, false)
            }
        }

        if (shouldToggle) parent.toggle()
        return removeChildCount
    }
}