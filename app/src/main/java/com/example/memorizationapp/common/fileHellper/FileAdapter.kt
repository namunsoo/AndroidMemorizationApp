package com.example.memorizationapp.common.fileHellper

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.databinding.ItemFolderBinding
import com.example.memorizationapp.databinding.ItemFileBinding
import com.example.memorizationapp.model.Data
import com.example.memorizationapp.ui.folder.FolderViewModel
import com.example.memorizationapp.ui.main.MainViewModel
import java.io.File

class FileAdapter(private val _mActivity: MainActivity, nodes: List<Node<Data>>) : TreeAdapter<Data, TreeViewHolder<Data>>(nodes) {
    private lateinit var mainViewModel : MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private val fileTreeCommon = FileTreeCommon

    override fun getItemViewType(position: Int): Int {
        val data = displayNodes[position]

        return if (data.content is Data.File) FILE else DIRECTORY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder<Data> {
        mainViewModel = ViewModelProvider(_mActivity)[MainViewModel::class.java]
        folderViewModel = ViewModelProvider(_mActivity)[FolderViewModel::class.java]

        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == FILE)
            FileViewHolder(
                ItemFileBinding.inflate(layoutInflater, null, false)
            )
        else
            DirectoryViewHolder(
                ItemFolderBinding.inflate(layoutInflater, null, false)
            )
    }

    override fun onBindViewHolder(holder: TreeViewHolder<Data>, position: Int) {
        val data = displayNodes[position]
        holder.bind(data)

        // 상위 경로 생성
        val name = data.content.name
        var path : String = ""
        var tempData = data
        while(!tempData.isRoot){
            path = "/" + tempData.parent?.content?.name + path
            tempData = tempData.parent!!
        }
        path = "$path/$name"

        val item : ConstraintLayout = (holder.itemView as ConstraintLayout).getChildAt(0) as ConstraintLayout
        val arrow = item.getViewById(R.id.iv_arrow) as ImageView
        if(data.isLeaf){
            arrow.setImageResource(0)
        } else {
            if(data.isExpand){
                arrow.setImageResource(R.drawable.ic_arrow_down)
            } else {
                arrow.setImageResource(R.drawable.ic_arrow_right)
            }
        }
        holder.itemView.setOnSingleClickListener {
            val isExpand = toggle(data)
            if(data.isLeaf){
                arrow.setImageResource(0)
            } else {
                if(isExpand){
                    arrow.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    arrow.setImageResource(R.drawable.ic_arrow_right)
                }
            }
        }

        val setting = item.getViewById(R.id.btn_setting) as ImageButton
        setting.setOnSingleClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.setForceShowIcon(true)
            popupMenu.menuInflater.inflate(R.menu.folder_setting, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_folder_create -> {
                        folderViewModel.setValues(path,"create","", data, position)
                        _mActivity.changeFragment(R.id.nav_folder)
                    }
                    R.id.action_folder_update -> {
                        folderViewModel.setValues(path,"update",name, data, position)
                        _mActivity.changeFragment(R.id.nav_folder)
                    }
                    R.id.action_folder_delete_with_sub -> {
                        deleteFolder(path, data)
                    }
                }
                false
            }

            popupMenu.show()
        }
    }

    private fun deleteFolder(path: String, data: Node<Data>){
        val folder = File(_mActivity.filesDir.absolutePath + path)
        var builder: AlertDialog.Builder = AlertDialog.Builder(_mActivity)
        builder.setMessage(R.string.dialog_folder_delete)
            .setTitle(R.string.dialog_title)
        // 확인 이벤트
        builder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
            dialog.dismiss()
            builder = AlertDialog.Builder(_mActivity)
            try {
                if (folder.exists() && folder.isDirectory) {
//                    if (!folder.deleteRecursively()) {
//                        builder.setMessage(R.string.dialog_folder_delete_error)
//                            .setTitle(R.string.dialog_error_title)
//                        builder.create().show()
//                    } else {
//                    }
                    val pathNodeList = fileTreeCommon.getTargetTree(data)
                    fileTreeCommon.deleteNode(mainViewModel.nodes, pathNodeList, 0)
                    mainViewModel.changeValueEvent()
                } else {
                    builder.setMessage(R.string.dialog_folder_not_exist)
                        .setTitle(R.string.dialog_error_title)
                    builder.create().show()
                }
            } catch(e: Exception) {
                builder.setMessage(R.string.dialog_folder_delete_error)
                    .setTitle(R.string.dialog_error_title)
                builder.create().show()
            }
        }
        // 취소 이벤트
        builder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
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
    private val binding: ItemFolderBinding
) : TreeViewHolder<Data>(binding.root) {

    override fun bind(data: Node<Data>) {
        if (data.content !is Data.Directory) return

        val padding = setPaddingStart(data)

        binding.tvName.text = data.content.name
        binding.clItem.layoutParams.width = Resources.getSystem().displayMetrics.widthPixels - padding
        if (data.children.isEmpty()) {
            binding.ivArrow.setImageResource(0)
        } else {
            binding.ivArrow.setImageResource(R.drawable.ic_arrow_right)
        }
    }
}