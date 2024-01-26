package com.example.memorizationapp.common.fileHellper

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class TreeViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected val context = itemView.context

    protected open val padding = DEFAULT_PADDING

    abstract fun bind(data: Node<T>)

    open fun setPaddingStart(data: Node<T>): Int = with(itemView) {
        val depth = data.depth
        itemView.setPadding(padding * depth, paddingTop, paddingRight, paddingBottom)
        return padding * depth
    }

    companion object {
        private const val DEFAULT_PADDING = 100
    }
}