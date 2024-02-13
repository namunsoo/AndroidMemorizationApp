package com.example.memorizationapp.ui.memorizationTest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel

class MemorizationTestFragment : Fragment() {

    private lateinit var memorizeOptionViewModel : MemorizeOptionViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        memorizeOptionViewModel = ViewModelProvider(requireActivity())[MemorizeOptionViewModel::class.java]
        _mActivity = activity as MainActivity

        // 상단바 지우기
        _mActivity.supportActionBar!!.hide()

        return inflater.inflate(R.layout.fragment_memorization_test, container, false)
    }


}