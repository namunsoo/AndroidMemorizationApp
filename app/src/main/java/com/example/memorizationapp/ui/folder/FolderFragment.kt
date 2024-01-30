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
import com.example.memorizationapp.ui.MainActivityViewModel
import com.example.memorizationapp.ui.main.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FolderFragment : Fragment() {

    private var _binding: FragmentFolderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mainViewModel : MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private lateinit var mainActivityViewModel : MainActivityViewModel
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
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        _mActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = folderViewModel.folderName.value.toString()
        val actionType = folderViewModel.action.value.toString()
        binding.folderName.setText(name)
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
                        val fileName: String = binding.folderName.text.toString()

                        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                        if (fileName.isEmpty()) {
                            builder.setMessage(R.string.dialog_folder_create).setTitle(R.string.dialog_title)
                            builder.create().show()
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
    private fun createFolder(name: String, context: Context) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val dialog: AlertDialog
        try {
            val data = mainActivityViewModel.fileTreeJson.value!!
            val jsonArray = JSONArray()
            val newObject = JSONObject()
            newObject.put("type", "folder")
            newObject.put("name", name)
            newObject.put("children", jsonArray)

            if (folderViewModel.node.value!!.isRoot){
                // MainViewModel 수정
                mainViewModel.nodes.add(Node(Data.Directory(name)))

                // MainActivityViewModel 수정
                data.getJSONArray("data").put(newObject)
                mainActivityViewModel.setFileTreeJson(data)
            } else {
                // MainViewModel 수정
                val pathNodeList = fileTreeCommon.getTargetTree(folderViewModel.node.value!!)
                fileTreeCommon.addNewNode(mainViewModel.nodes, pathNodeList, 0, Node(Data.Directory(name)))

                // MainActivityViewModel 수정
                val nameList = fileTreeCommon.getTargetJson(folderViewModel.node.value!!)
                fileTreeCommon.createJsonObject(data.getJSONArray("data"), nameList, 0, newObject)
            }

            // json 파일 수정
            val fileTree = File(_mActivity.filesDir.absolutePath, "file_tree.json")
            fileTree.writeText(data.toString())
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
        val dialog: AlertDialog

        try {
            val data = mainActivityViewModel.fileTreeJson.value!!

            // MainViewModel 수정
            val pathNodeList = fileTreeCommon.getTargetTree(folderViewModel.node.value!!)
            fileTreeCommon.updateNode(mainViewModel.nodes, pathNodeList, 0, newFolderName)

            // MainActivityViewModel 수정
            if (folderViewModel.node.value!!.isRoot){
                fileTreeCommon.findJsonObjectByName(data.getJSONArray("data"), newFolderName).put("name", newFolderName)
                mainActivityViewModel.setFileTreeJson(data)
            } else {
                val nameList = fileTreeCommon.getTargetJson(folderViewModel.node.value!!)
                fileTreeCommon.updateJsonObject(data.getJSONArray("data"), nameList, 0, newFolderName)
            }

            // json 파일 수정
            val fileTree = File(_mActivity.filesDir.absolutePath, "file_tree.json")
            fileTree.writeText(data.toString())
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