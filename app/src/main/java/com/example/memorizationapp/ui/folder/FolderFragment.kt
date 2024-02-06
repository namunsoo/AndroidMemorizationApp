package com.example.memorizationapp.ui.folder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.common.treeRecyclerView.FolderTreeCommon
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.databinding.FragmentFolderBinding
import com.example.memorizationapp.ui.main.MainViewModel


class FolderFragment : Fragment() {

    private var _binding: FragmentFolderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val folderTreeCommon = FolderTreeCommon

    private lateinit var mainViewModel: MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private lateinit var _mActivity : MainActivity

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
        val actionType = folderViewModel.action.value.toString()
        when (actionType) {
            CREATE -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_folder_create)
            }
            UPDATE -> {
                val name = folderViewModel.model.value!!.content.name
                binding.folderName.setText(name)
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
                    R.id.action_check -> {
                        if (actionType == CREATE) {
                            if(!createFolder()){
                                return false
                            }
                        } else {
                            if(!updateFolder()){
                                return false
                            }
                        }
                        _mActivity.goBack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createFolder(): Boolean{
        val name: String = binding.folderName.text.toString()
        if (!valueCheck()) return false

        val dbHelper = DBHelper(_mActivity)
        if (folderViewModel.model.value == null) {
            val main = dbHelper.insertMainFolder(name)
            mainViewModel.modelList.value!!.add(main)
        } else {
            val mainId = folderViewModel.model.value!!.content.id
            val sub = dbHelper.insertSubFolder(name, mainId)
            val pathList = folderTreeCommon.getTargetTree(folderViewModel.model.value!!)
            folderTreeCommon.addNewModel(mainViewModel.modelList.value!!, pathList, 0, sub)
        }
        dbHelper.close()
        return true
    }

    private fun updateFolder(): Boolean{
        val name: String = binding.folderName.text.toString()
        if (!valueCheck()) return false

        val dbHelper = DBHelper(_mActivity)
        val model = folderViewModel.model.value!!
        when(folderViewModel.model.value!!.content){
            is Item.MainFolder -> {
                dbHelper.updateMainFolder(model, name)
            }
            is Item.SubFolder -> {
                dbHelper.updateSubFolder(model, name)
            }
            else -> {
                dbHelper.close()
                throw Exception("update folder error [update target not exist or not folder]")
            }
        }
        val pathList = folderTreeCommon.getTargetTree(model)
        folderTreeCommon.updateModel(mainViewModel.modelList.value!!, pathList, 0, name)
        dbHelper.close()
        return true
    }

    private fun valueCheck(): Boolean {
        val name: String = binding.folderName.text.toString()
        val builder: AlertDialog.Builder = AlertDialog.Builder(_mActivity)
        if (name.isEmpty()) {
            builder.setMessage(R.string.dialog_folder_create).setTitle(R.string.dialog_title)
            builder.create().show()
            binding.folderName.requestFocus()
            val imm = _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(binding.folderName, 0)
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CREATE = "create"
        private const val UPDATE = "update"
    }

}