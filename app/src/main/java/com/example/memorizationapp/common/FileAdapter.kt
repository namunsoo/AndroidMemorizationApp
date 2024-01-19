package com.example.memorizationapp.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.memorizationapp.databinding.ItemDirectoryBinding
import com.example.memorizationapp.databinding.ItemFileBinding
import com.example.memorizationapp.model.Data

class FileAdapter(nodes: List<Node<Data>>) : TreeAdapter<Data, TreeViewHolder<Data>>(nodes) {

    override fun getItemViewType(position: Int): Int {
        val data = displayNodes[position]

        return if (data.content is Data.File) FILE else DIRECTORY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder<Data> {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == FILE)
            FileViewHolder(
                ItemFileBinding.inflate(layoutInflater, null, false)
            )
        else
            DirectoryViewHolder(
                ItemDirectoryBinding.inflate(layoutInflater, null, false)
            )
    }

    override fun onBindViewHolder(holder: TreeViewHolder<Data>, position: Int) {
        val data = displayNodes[position]
        holder.bind(data)

        holder.itemView.setOnSingleClickListener {
            toggle(data)
        }
    }

    companion object {
        private const val FILE = 0
        private const val DIRECTORY = 1
    }
}

class FileViewHolder(
    private val binding: ItemFileBinding
) : TreeViewHolder<Data>(binding.root) {

    override fun bind(data: Node<Data>) {
        if (data.content !is Data.File) return

        setPaddingStart(data)

        binding.tvName.text = data.content.name
    }
}

class DirectoryViewHolder(
    private val binding: ItemDirectoryBinding
) : TreeViewHolder<Data>(binding.root) {

    override fun bind(data: Node<Data>) {
        if (data.content !is Data.Directory) return

        setPaddingStart(data)

        binding.tvName.text = data.content.name
    }
}