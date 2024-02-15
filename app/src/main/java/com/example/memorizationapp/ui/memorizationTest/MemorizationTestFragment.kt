package com.example.memorizationapp.ui.memorizationTest

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.databinding.FragmentMemorizationTestBinding
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel

class MemorizationTestFragment : Fragment() {

    private var _binding: FragmentMemorizationTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var memorizeOptionViewModel : MemorizeOptionViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemorizationTestBinding.inflate(inflater, container, false)

        memorizeOptionViewModel = ViewModelProvider(requireActivity())[MemorizeOptionViewModel::class.java]
        _mActivity = activity as MainActivity

        // 상단바 지우기
        _mActivity.supportActionBar!!.hide()

        binding.btnTestEnd.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(_mActivity)
            alertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_memorization_test_end)

            alertDialogBuilder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
                _mActivity.changeFragment(R.id.nav_main)
                _mActivity.supportActionBar!!.show()
                dialog.dismiss()
            }

            alertDialogBuilder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.vpMemorization
        val adapter = MemorizationTestAdapter(_mActivity, binding.tvProgress, binding.sbProgress,getString(R.string.common_progress))
        viewPager.adapter = adapter
        // 여러가지 이유로 복작하게 설계 (일단 테스트는 정상)
        var cardCenterIndex = 2 // 페이지 5개로 재사용? 해서 그 중간이
        var lastPosition = viewPager.currentItem
        var progressEvent = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // onPageSelected는 여러번 불러서 한번만 하도록
                if (position != lastPosition) {
                    if (!progressEvent) { // SeekBar (@+id/sb_progress) 변경했을 경우는 로직 수행 X
                        val viewPagerAdapter = (viewPager.adapter as MemorizationTestAdapter)
                        if (position > lastPosition) {
                            cardCenterIndex = viewPagerAdapter.addNextItem(position, cardCenterIndex)
                        } else {
                            cardCenterIndex = viewPagerAdapter.addBeforeItem(position, cardCenterIndex)
                        }
                    } else {
                        progressEvent = false
                    }
                    lastPosition = position
                }
            }
        })
        binding.sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // This method is called when the progress value of the seek bar changes.
                // You can update UI or perform any action based on the new progress value.
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // This method is called when the user starts interacting with the seek bar.
                // You can perform any necessary setup or actions here.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar!!.max != 0) {
                    val viewPagerAdapter = (viewPager.adapter as MemorizationTestAdapter)
                    progressEvent = true
                    cardCenterIndex = viewPagerAdapter.getCardCenter(seekBar!!.progress)
                    val index = viewPagerAdapter.setTargetCardItems(seekBar!!.progress)
                    viewPager.setCurrentItem(index, true)
                }
            }
        })
    }

}