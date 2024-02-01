package com.example.memorizationapp.common.treeRecyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class TreeViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected val context = itemView.context

    protected open val padding = DEFAULT_PADDING

    abstract fun bind(data: Model<T>)

    open fun setPaddingStart(data: Model<T>): Int = with(itemView) {
        val depth = data.depth
        itemView.setPadding(padding * depth, paddingTop, paddingRight, paddingBottom)
        return padding * depth
    }

    companion object {
        private const val DEFAULT_PADDING = 100
    }
}