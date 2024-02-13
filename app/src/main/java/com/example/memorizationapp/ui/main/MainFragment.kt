package com.example.memorizationapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.common.treeRecyclerView.FolderTreeAdapter
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model
import com.example.memorizationapp.databinding.FragmentMainBinding
import com.example.memorizationapp.ui.file.FileViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var _hiddenPanelMain: RelativeLayout? = null
    private var _hiddenPanelContent: RelativeLayout? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private lateinit var fileViewModel : FileViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        _hiddenPanelMain = binding.hiddenPanelMain
        _hiddenPanelContent = binding.hiddenPanelContent
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        folderViewModel = ViewModelProvider(requireActivity())[FolderViewModel::class.java]
        fileViewModel = ViewModelProvider(requireActivity())[FileViewModel::class.java]
        _mActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionHiddenPanelAnim()

        // 파일 생성 연결
        binding.btnCreateFile.setOnClickListener {
            fileViewModel.setValue(null, "create") // item class 만 설정
            _mActivity.changeFragment(R.id.nav_file)
        }

        // 폴더 생성 연결
        binding.btnCreateFolder.setOnClickListener {
            folderViewModel.setValue(null, "create") // item class 만 설정
            _mActivity.changeFragment(R.id.nav_folder)
        }

        mainViewModel.modelList.observe(_mActivity, Observer {
            getFolderTree()
        })
    }

    private fun getFolderTree() {
        val models: MutableList<Model<Item>>
        if(mainViewModel.modelList.value.isNullOrEmpty()){
            val dbHelper = DBHelper(_mActivity)
            models = dbHelper.getFolderTree()
            mainViewModel.setValue(models)
            dbHelper.close()
        } else {
            models = mainViewModel.modelList.value!!
        }
        val recyclerView = binding.rvFolderList
        recyclerView.adapter = FolderTreeAdapter(_mActivity, models.toList())
    }

    // 패널 애니메이션 연결
    private fun connectionHiddenPanelAnim() {
        binding.fabOpenAddOptions.setOnClickListener {
            hiddenPanelAnim()
        }
        _hiddenPanelMain?.setOnClickListener {
            if (it.id == _hiddenPanelMain!!.id) {
                hiddenPanelAnim()
            }
        }
    }

    // 패널 애니메이션
    private fun hiddenPanelAnim() {
        if (!isPanelShown()) {
            // Show the panel
            val fadeIn = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.fade_in
            )
            val bottomUp = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.bottom_up
            )
            _hiddenPanelMain?.startAnimation(fadeIn)
            _hiddenPanelContent?.startAnimation(bottomUp)
            _hiddenPanelMain?.visibility = View.VISIBLE
        } else {
            // Hide the Panel
            val fadeOut = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.fade_out
            )
            val bottomDown = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.bottom_down
            )
            _hiddenPanelContent?.startAnimation(bottomDown)
            _hiddenPanelMain?.startAnimation(fadeOut)
            _hiddenPanelMain?.visibility = View.GONE
        }
    }

    // 패널 보여짐 여부
    private fun isPanelShown(): Boolean {
        return _hiddenPanelMain?.visibility == View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}