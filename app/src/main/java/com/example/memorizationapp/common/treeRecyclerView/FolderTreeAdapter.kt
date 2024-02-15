package com.example.memorizationapp.common.treeRecyclerView

import android.app.AlertDialog
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.databinding.ItemFileBinding
import com.example.memorizationapp.databinding.ItemFolderBinding
import com.example.memorizationapp.ui.cardList.CardListViewModel
import com.example.memorizationapp.ui.file.FileViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel
import com.example.memorizationapp.ui.main.MainViewModel

class FolderTreeAdapter (private val _mActivity: MainActivity, models: List<Model<Item>>) : TreeAdapter<Item, TreeViewHolder<Item>>(models) {

    private val folderTreeCommon = FolderTreeCommon
    private lateinit var mainViewModel: MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private lateinit var fileViewModel : FileViewModel
    private lateinit var cardListViewModel: CardListViewModel
    override fun getItemViewType(position: Int): Int {
        val data = displayItems[position]

        return if (data.content is Item.CardBundle) FILE else FOLDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder<Item> {
        mainViewModel = ViewModelProvider(_mActivity)[MainViewModel::class.java]
        folderViewModel = ViewModelProvider(_mActivity)[FolderViewModel::class.java]
        fileViewModel = ViewModelProvider(_mActivity)[FileViewModel::class.java]
        cardListViewModel = ViewModelProvider(_mActivity)[CardListViewModel::class.java]

        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == FILE)
            FileViewHolder(
                ItemFileBinding.inflate(layoutInflater, null, false)
            )
        else
            FolderViewHolder(
                ItemFolderBinding.inflate(layoutInflater, null, false)
            )
    }

    override fun onBindViewHolder(holder: TreeViewHolder<Item>, position: Int) {
        val data = displayItems[position]
        holder.bind(data)

        val item : ConstraintLayout = (holder.itemView as ConstraintLayout).getChildAt(0) as ConstraintLayout

        if (data.content is Item.CardBundle) {
            holder.itemView.setOnSingleClickListener {
                cardListViewModel.setValue(data.content.id, mutableListOf())
                _mActivity.changeFragment(R.id.nav_card_list)
            }
        }
        else {
            val arrow = item.getViewById(R.id.iv_arrow) as ImageView
            if(data.haveChildren){
                if(data.isOpen){
                    arrow.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    arrow.setImageResource(R.drawable.ic_arrow_right)
                }
            } else {
                arrow.setImageResource(0)
            }
            holder.itemView.setOnSingleClickListener {
                val isOpen = toggle(data)
                if(data.haveChildren){
                    if(isOpen){
                        arrow.setImageResource(R.drawable.ic_arrow_down)
                    } else {
                        arrow.setImageResource(R.drawable.ic_arrow_right)
                    }
                } else {
                    arrow.setImageResource(0)
                }
            }
        }

        val setting = item.getViewById(R.id.btn_setting) as ImageButton
        setting.setOnSingleClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.setForceShowIcon(true)
            if (data.content is Item.CardBundle) {
                popupMenu.menuInflater.inflate(R.menu.file_setting, popupMenu.menu)
            } else {
                popupMenu.menuInflater.inflate(R.menu.folder_setting, popupMenu.menu)
                if (data.content is Item.SubFolder) {
                    popupMenu.menu.findItem(R.id.action_folder_create).isVisible = false
                }
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_file_create -> {
                        fileViewModel.setValue(data, CREATE)
                        _mActivity.changeFragment(R.id.nav_file)
                    }
                    R.id.action_file_update -> {
                        fileViewModel.setValue(data, UPDATE)
                        _mActivity.changeFragment(R.id.nav_file)
                    }
                    R.id.action_file_delete -> {
                        deleteFolderOrFile(data)
                    }
                    R.id.action_folder_create -> {
                        folderViewModel.setValue(data, CREATE)
                        _mActivity.changeFragment(R.id.nav_folder)
                    }
                    R.id.action_folder_update -> {
                        folderViewModel.setValue(data, UPDATE)
                        _mActivity.changeFragment(R.id.nav_folder)
                    }
                    R.id.action_folder_delete -> {
                        deleteFolderOrFile(data)
                    }
                }
                false
            }

            popupMenu.show()
        }
    }

    private fun deleteFolderOrFile(model: Model<Item>){
        val alertDialogBuilder = AlertDialog.Builder(_mActivity)
        alertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_delete)

        alertDialogBuilder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
            val dbHelper = DBHelper(_mActivity)
            when(model.content){
                is Item.MainFolder -> {
                    dbHelper.deleteMainFolder(model.content.id)
                    dbHelper.deleteSubFolders(model.children)
                    dbHelper.deleteCardBundleWithMainFolderId(model.content.id)
                }
                is Item.SubFolder -> {
                    dbHelper.deleteSubFolder(model.content.id)
                    dbHelper.deleteCardBundleWithSubFolderId(model.content.id)
                }
                is Item.CardBundle -> {
                    dbHelper.deleteCardBundle(model.content.id)
                }
            }
            dbHelper.close()
            val pathList = folderTreeCommon.getTargetTree(model)
            folderTreeCommon.deleteModel(mainViewModel.modelList.value!!, pathList, 0)
            mainViewModel.setValue(mainViewModel.modelList.value!!)
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
        var lastClickTime = System.currentTimeMillis()

        setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < 500) return@setOnClickListener

            lastClickTime = System.currentTimeMillis()

            onSingleClick(this)
        }
    }

    companion object {
        private const val FILE = 0
        private const val FOLDER = 1
        private const val CREATE = "create"
        private const val UPDATE = "update"
    }
}

class FileViewHolder(
    private val binding: ItemFileBinding
) : TreeViewHolder<Item>(binding.root) {

    override fun bind(data: Model<Item>) {
        if (data.content !is Item.CardBundle) return

        val padding = setPaddingStart(data)

        binding.tvName.text = data.content.name
        binding.clItem.layoutParams.width = Resources.getSystem().displayMetrics.widthPixels - padding
    }
}

class FolderViewHolder(
    private val binding: ItemFolderBinding
) : TreeViewHolder<Item>(binding.root) {

    override fun bind(data: Model<Item>) {
        if (data.content is Item.CardBundle) return

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