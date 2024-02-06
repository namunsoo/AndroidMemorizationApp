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
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.common.treeRecyclerView.FolderTreeCommon
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model
import com.example.memorizationapp.databinding.FragmentFileBinding
import com.example.memorizationapp.ui.main.MainViewModel

class FileFragment : Fragment() {

    private var _binding: FragmentFileBinding? = null

    private val folderTreeCommon = FolderTreeCommon
    private lateinit var mainViewModel: MainViewModel
    private lateinit var fileViewModel : FileViewModel
    private lateinit var _mActivity : MainActivity

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileBinding.inflate(inflater, container, false)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        fileViewModel = ViewModelProvider(requireActivity())[FileViewModel::class.java]
        _mActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionType = fileViewModel.action.value.toString()
        when (actionType) {
            CREATE -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_file_create)
            }
            UPDATE -> {
                val name = fileViewModel.model.value!!.content.name
                binding.fileName.setText(name)
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
                    R.id.action_check -> {
                        if (actionType == CREATE) {
                            if(!createFile()){
                                return false
                            }
                        } else {
                            if(!updateFile()){
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
    private fun createFile(): Boolean{
        val name: String = binding.fileName.text.toString()
        if (!valueCheck()) return false

        val dbHelper = DBHelper(_mActivity)
        val cardBundle: Model<Item>
        if (fileViewModel.model.value == null) {
            cardBundle = dbHelper.insertCardBundle(name)
            mainViewModel.modelList.value!!.add(cardBundle)
        } else {
            when (fileViewModel.model.value!!.content) {
                is Item.MainFolder -> {
                    cardBundle = dbHelper.insertCardBundle(name, fileViewModel.model.value!!.content.id)
                }
                is Item.SubFolder -> {
                    cardBundle = dbHelper.insertCardBundle(name,
                        fileViewModel.model.value!!.content.main_id,
                        fileViewModel.model.value!!.content.id)
                }
                else -> {
                    dbHelper.close()
                    throw Exception("create file(card) error [there is not folder]")
                }
            }
            val pathList = folderTreeCommon.getTargetTree(fileViewModel.model.value!!)
            folderTreeCommon.addNewModel(mainViewModel.modelList.value!!, pathList, 0, cardBundle)
        }
        dbHelper.createNewCardTable(cardBundle.content.id)
        dbHelper.close()
        return true
    }
    private fun updateFile(): Boolean{
        val name: String = binding.fileName.text.toString()
        if (!valueCheck()) return false

        val dbHelper = DBHelper(_mActivity)
        dbHelper.updateCardBundle(fileViewModel.model.value!!, name)
        val pathList = folderTreeCommon.getTargetTree(fileViewModel.model.value!!)
        folderTreeCommon.updateModel(mainViewModel.modelList.value!!, pathList, 0, name)
        dbHelper.close()
        return true
    }

    private fun valueCheck(): Boolean {
        val name: String = binding.fileName.text.toString()
        val builder: AlertDialog.Builder = AlertDialog.Builder(_mActivity)
        if (name.isEmpty()) {
            builder.setMessage(R.string.dialog_file_create).setTitle(R.string.dialog_title)
            builder.create().show()
            binding.fileName.requestFocus()
            val imm = _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(binding.fileName, 0)
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