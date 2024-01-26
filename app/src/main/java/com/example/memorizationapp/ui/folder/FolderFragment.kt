package com.example.memorizationapp.ui.folder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.fileHellper.FileTreeCommon
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.databinding.FragmentFolderBinding
import com.example.memorizationapp.model.Data
import com.example.memorizationapp.ui.main.MainViewModel
import java.io.File

class FolderFragment : Fragment() {

    private var _binding: FragmentFolderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mainViewModel : MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private lateinit var _mActivity : MainActivity
    private val fileTreeCommon = FileTreeCommon

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        folderViewModel = ViewModelProvider(requireActivity())[FolderViewModel::class.java]
        _mActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = folderViewModel.folderName.value.toString()
        val actionType = folderViewModel.action.value.toString()
        binding.fileName.setText(name)
        when (actionType) {
            "create" -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_folder_create)
            }
            "update" -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_folder_update)
            }
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            // 기존 menu 지우고 check menu 설정
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.check, menu)
            }

            // menu 클릭 이벤트 설정
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.check -> {
                        val fileName: String = binding.fileName.text.toString()

                        if (fileName.isEmpty()) {
                            val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                            builder.setMessage(R.string.dialog_folder_create).setTitle(R.string.dialog_title)
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                            return false
                        }
                        if (actionType == "create") { createFolder(fileName, context!!) }
                        else { updateFolder(fileName, context!!) }
                        _mActivity.goBack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // 폴더 생성
    private fun createFolder(fileName: String, context: Context) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var dialog: AlertDialog
        try {
            // 파일 생성
            val parentPath = folderViewModel.folderPath.value.toString()
            val dir = File(_mActivity.filesDir.absolutePath +parentPath, fileName)
            if (dir.exists()) {
                builder.setMessage(R.string.dialog_folder_exist)
                    .setTitle(R.string.dialog_error_title)
                dialog = builder.create()
                dialog.show()
            } else {
                dir.mkdirs()
            }

            // MainViewModel 수정
            if (folderViewModel.position.value == null){
                mainViewModel.nodes.add(Node(Data.Directory(fileName)))
            } else {
                val pathNodeList = fileTreeCommon.getTargetTree(folderViewModel.node.value!!)
                fileTreeCommon.addNewNode(mainViewModel.nodes, pathNodeList, 0, Node(Data.Directory(fileName)))
            }
        } catch(e: Exception) {
            builder.setMessage(R.string.dialog_folder_create_error)
                .setTitle(R.string.dialog_error_title)
            dialog = builder.create()
            dialog.show()
        }
    }

    // 폴더 이름 수정
    private fun updateFolder(newFolderName: String, context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var dialog: AlertDialog
        val oldFolderPath = folderViewModel.folderPath.value.toString()
        val oldFolder = File(_mActivity.filesDir.absolutePath + oldFolderPath)

        try {
            if (oldFolder.exists() && oldFolder.isDirectory && folderViewModel.position.value != null) {
                val newFolder = File(oldFolder.parent!!,newFolderName)

                if (!oldFolder.renameTo(newFolder)) {
                    builder.setMessage(R.string.dialog_folder_update_error)
                        .setTitle(R.string.dialog_error_title)
                    dialog = builder.create()
                    dialog.show()
                } else {
                    // MainViewModel 수정
                    val pathNodeList = fileTreeCommon.getTargetTree(folderViewModel.node.value!!)
                    fileTreeCommon.updateNode(mainViewModel.nodes, pathNodeList, 0, newFolderName)
                }

            } else {
                builder.setMessage(R.string.dialog_folder_not_exist)
                    .setTitle(R.string.dialog_error_title)
                dialog = builder.create()
                dialog.show()
            }
        } catch(e: Exception) {
            builder.setMessage(R.string.dialog_folder_update_error)
                .setTitle(R.string.dialog_error_title)
            dialog = builder.create()
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}