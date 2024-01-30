package com.example.memorizationapp.ui.file

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.fileHellper.FileTreeCommon
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.databinding.FragmentFileBinding
import com.example.memorizationapp.model.Data
import com.example.memorizationapp.ui.MainActivityViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel
import com.example.memorizationapp.ui.main.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FileFragment : Fragment() {

    private var _binding: FragmentFileBinding? = null

    private lateinit var mainViewModel : MainViewModel
    private lateinit var fileViewModel: FileViewModel
    private lateinit var mainActivityViewModel : MainActivityViewModel
    private lateinit var _mActivity : MainActivity
    private val fileTreeCommon = FileTreeCommon

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileBinding.inflate(inflater, container, false)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        fileViewModel = ViewModelProvider(requireActivity())[FileViewModel::class.java]
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        _mActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = fileViewModel.fileName.value.toString()
        val actionType = fileViewModel.action.value.toString()
        binding.fileName.setText(name)
        when (actionType) {
            "create" -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_file_create)
            }
            "update" -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_file_update)
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

                        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                        if (fileName.isEmpty()) {
                            builder.setMessage(R.string.dialog_folder_create).setTitle(R.string.dialog_title)
                            builder.create().show()
                            return false
                        }
                        if (actionType == "create") { createFile(fileName, context!!) }
                        else { updateFile(fileName, context!!) }
                        _mActivity.goBack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // 폴더 생성
    private fun createFile(name: String, context: Context) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val dialog: AlertDialog
        try {
            val data = mainActivityViewModel.fileTreeJson.value!!
            val jsonArray = JSONArray()
            val newObject = JSONObject()
            newObject.put("type", "file")
            newObject.put("name", name)
            newObject.put("table", name)
            newObject.put("children", jsonArray)

            if (fileViewModel.node.value!!.isRoot){
                // MainViewModel 수정
                mainViewModel.nodes.add(Node(Data.File(name)))

                // MainActivityViewModel 수정
                data.getJSONArray("data").put(newObject)
                mainActivityViewModel.setFileTreeJson(data)
            } else {
                // MainViewModel 수정
                val pathNodeList = fileTreeCommon.getTargetTree(fileViewModel.node.value!!)
                fileTreeCommon.addNewNode(mainViewModel.nodes, pathNodeList, 0, Node(Data.File(name)))

                // MainActivityViewModel 수정
                val nameList = fileTreeCommon.getTargetJson(fileViewModel.node.value!!)
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
    private fun updateFile(newFileName: String, context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val dialog: AlertDialog

        try {
            val data = mainActivityViewModel.fileTreeJson.value!!

            // MainViewModel 수정
            val pathNodeList = fileTreeCommon.getTargetTree(fileViewModel.node.value!!)
            fileTreeCommon.updateNode(mainViewModel.nodes, pathNodeList, 0, newFileName)

            // MainActivityViewModel 수정
            if (fileViewModel.node.value!!.isRoot){
                fileTreeCommon.findJsonObjectByName(data.getJSONArray("data"), newFileName).put("name", newFileName)
                mainActivityViewModel.setFileTreeJson(data)
            } else {
                val nameList = fileTreeCommon.getTargetJson(fileViewModel.node.value!!)
                fileTreeCommon.updateJsonObject(data.getJSONArray("data"), nameList, 0, newFileName)
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