package com.example.memorizationapp.ui.main

import android.database.Cursor
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
import com.example.memorizationapp.common.database.DB
import com.example.memorizationapp.common.fileHellper.FileAdapter
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.databinding.FragmentMainBinding
import com.example.memorizationapp.model.Data
import com.example.memorizationapp.ui.MainActivityViewModel
import com.example.memorizationapp.ui.file.FileViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel
import org.json.JSONObject


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var _hiddenPanelMain: RelativeLayout? = null
    private var _hiddenPanelContent: RelativeLayout? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var folderViewModel : FolderViewModel
    private lateinit var fileViewModel: FileViewModel
    private lateinit var mainActivityViewModel : MainActivityViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        _hiddenPanelMain = binding.hiddenPanelMain
        _hiddenPanelContent = binding.hiddenPanelContent
        _mActivity = activity as MainActivity

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        folderViewModel = ViewModelProvider(requireActivity())[FolderViewModel::class.java]
        fileViewModel = ViewModelProvider(requireActivity())[FileViewModel::class.java]
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionHiddenPanelAnim()

        // 파일 생성 연결
        binding.bntCreateFile.setOnClickListener {
            fileViewModel.setValues("create","")
            _mActivity.changeFragment(R.id.nav_file)
        }

        // 폴더 생성 연결
        binding.bntCreateFolder.setOnClickListener {
            folderViewModel.setValues("create","")
            _mActivity.changeFragment(R.id.nav_folder)
        }

        mainActivityViewModel.fileTreeJson.observe(viewLifecycleOwner, Observer {
            getFolderTree()
        })
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

    // 폴더 트리 가져오기
    private fun getFolderTree() {
        var list = listOf<Node<Data>>()
        if(mainActivityViewModel.fileTreeJson.value != null && mainViewModel.nodes.isEmpty()){
            val jsonObject = mainActivityViewModel.fileTreeJson.value!!
            val jsonArray = jsonObject.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                list = list + getNodeFromJson(jsonArray.getJSONObject(i))
            }
            mainViewModel.nodes = list.toMutableList()
        } else {
            list = mainViewModel.nodes
        }
        val recyclerView = binding.recyclerViewFolderList
        recyclerView.adapter = FileAdapter(_mActivity,list)
    }

    // json 파일에서 트리 데이터 가져오기
    private fun getNodeFromJson(jsonObject: JSONObject): Node<Data> {
        val item: Node<Data>
        if (jsonObject.getString("type").equals("folder")) {
            item = Node(Data.Directory(jsonObject.getString("name")))
            val children = jsonObject.getJSONArray("children")
            for (i in 0 until children.length()) {
                item.addChild(getNodeFromJson(children.getJSONObject(i)))
            }
        } else {
            item = Node(Data.File(jsonObject.getString("name")))
        }
        return item
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}