package com.example.memorizationapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.memorizationapp.R
import com.example.memorizationapp.databinding.FragmentMainBinding
import com.example.memorizationapp.ui.folder.FolderActivity
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

        binding.buttonTest.setOnClickListener {
            val intent = Intent(requireContext(), FolderActivity::class.java)
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
//        Log.d(TAG, "Internal app's cache dir: " + context?.cacheDir?.absolutePath)
//        Log.d(TAG, "Internal app's file dir: " + context?.filesDir?.getAbsolutePath())
        //val asdf = File(context?.filesDir, "Pictures")
        //val directory = File(Environment.getExternalStorageDirectory().absolutePath)
        val directory = File(context?.filesDir?.getAbsolutePath())
        //val directory = File("/data/user/0/com.example.memorizationapp/files")
        var test: String = ""
        // Check if the path points to a directory
        if (directory.isDirectory) {
            // Get an array of File objects representing the files and directories in the specified directory
            val files = directory.listFiles()

            if (files != null) {
                // Iterate through the files and print the names of directories
                for (file in files) {
                    if (file.isDirectory) {
                        test += "Folder: ${file.name}"
                    }
                }
            } else {
                test += "No files found in the directory."
            }
        } else {
            test += "The specified path is not a directory."
        }

        val textView = binding.testTv
        textView.text = test
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}