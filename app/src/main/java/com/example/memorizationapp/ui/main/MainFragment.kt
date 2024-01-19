package com.example.memorizationapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.memorizationapp.R
import com.example.memorizationapp.common.FileAdapter
import com.example.memorizationapp.common.Node
import com.example.memorizationapp.databinding.FragmentMainBinding
import com.example.memorizationapp.model.Data
import com.example.memorizationapp.ui.folder.CreateFolderActivity
import java.io.File


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var _hiddenPanelMain: RelativeLayout? = null
    private var _hiddenPanelContent: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _hiddenPanelMain = binding.hiddenPanelMain
        _hiddenPanelContent = binding.hiddenPanelContent
        connectionHiddenPanelAnim()

        // 임시 파일 생성 연결
        binding.buttonTest.setOnClickListener {
            val intent = Intent(requireContext(), CreateFolderActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        getFolderTree()
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
            _hiddenPanelMain?.setVisibility(View.VISIBLE)
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
            _hiddenPanelMain?.setVisibility(View.GONE)
        }
    }

    // 패널 보여짐 여부
    private fun isPanelShown(): Boolean {
        return _hiddenPanelMain?.visibility == View.VISIBLE
    }

    // 폴더 트리 가져와서 바인딩
    private fun getFolderTree() {
        val directory = File(context?.filesDir?.getAbsolutePath())
        var list: List<Node<Data>> = listOf()
        if (directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    list = list + getFolderAndFile(file)
                }
            }
        }
        val recyclerView = binding.recyclerViewFolderList
        recyclerView.adapter = FileAdapter(list)
    }

    // 파일 또는 폴더 가져오기
    private fun getFolderAndFile(directory: File): Node<Data> {
        var item: Node<Data>? = null
        if (directory.isDirectory) {
            item = Node<Data>(Data.Directory(directory.name))
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    item.addChild(getFolderAndFile(file))
                }
            }
        } else {
            item = Node<Data>(Data.File(directory.name))
        }
        return item
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}