package com.example.memorizationapp.ui.memorizationTest

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.databinding.FragmentMemorizationTestBinding
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel

class MemorizationTestFragment : Fragment() {

    private var _binding: FragmentMemorizationTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var memorizationTestViewModel: MemorizationTestViewModel
    private lateinit var memorizeOptionViewModel : MemorizeOptionViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemorizationTestBinding.inflate(inflater, container, false)

        memorizationTestViewModel = ViewModelProvider(requireActivity())[MemorizationTestViewModel::class.java]
        memorizeOptionViewModel = ViewModelProvider(requireActivity())[MemorizeOptionViewModel::class.java]
        _mActivity = activity as MainActivity
        _mActivity.stopNavigationDrawer()

        // 상단바 지우기
        _mActivity.supportActionBar!!.hide()

        binding.btnTestEnd.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(_mActivity)
            alertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_memorization_test_end)

            alertDialogBuilder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
                _mActivity.startNavigationDrawer()
                _mActivity.changeFragment(R.id.nav_main)
                _mActivity.supportActionBar!!.show()
                this.onDestroy()
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

        var isFromOtherEvent = false
        // 수정후 파일 업데이트
        if(memorizationTestViewModel.cardCenterIndex == null || memorizationTestViewModel.cardIndex == null) {
            memorizationTestViewModel.cardCenterIndex = 2 // 페이지 5개로 재사용? 해서 그 중간이
        } else {
            isFromOtherEvent = true
            setAdapterNewItems(viewPager, memorizationTestViewModel.cardIndex!! + 1)
        }

        // 여러가지 이유로 복작하게 설계 (일단 테스트는 정상)
        var lastPosition = viewPager.currentItem
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // onPageSelected는 여러번 불러서 한번만 하도록
                if (position != lastPosition) {
                    if (!isFromOtherEvent) { // 스크롤이 아니라 변도 이벤트로 변경했을 경우는 로직 수행 X
                        val viewPagerAdapter = (viewPager.adapter as MemorizationTestAdapter)
                        if (position > lastPosition) {
                            memorizationTestViewModel.cardCenterIndex = viewPagerAdapter.addNextItem(position, memorizationTestViewModel.cardCenterIndex!!)
                        } else {
                            memorizationTestViewModel.cardCenterIndex = viewPagerAdapter.addBeforeItem(position, memorizationTestViewModel.cardCenterIndex!!)
                        }
                    } else {
                        isFromOtherEvent = false
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
                    isFromOtherEvent = true
                    setAdapterNewItems(viewPager, seekBar!!.progress)
                }
            }
        })
        binding.btnMemorizedFail.setOnClickListener{
            val viewPagerAdapter = (viewPager.adapter as MemorizationTestAdapter)
            viewPagerAdapter.setCurrentCardMemorized(viewPager.currentItem, false, memorizationTestViewModel.cardCenterIndex!!)
            if (viewPager.currentItem + 1 <= viewPagerAdapter.getMaxIndex()) {
                isFromOtherEvent = true
                lastPosition = viewPager.currentItem + 1
                memorizationTestViewModel.cardCenterIndex = viewPagerAdapter.addNextItem(viewPager.currentItem + 1, memorizationTestViewModel.cardCenterIndex!!)

                // 도저히 이방법 말고는 못찾음
                // 딜레이 안주면 정상적으로 작동하지 않는경우가 있음
                // 내 예상은 아이템이 정렬하기 전에 setCurrentItem이 먼저 실행되는거 같음
                val delayMillis = 200 // 0.2 초
                Handler().postDelayed({
                    viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                }, delayMillis.toLong())

            } else {
                val alertDialogBuilder = AlertDialog.Builder(_mActivity)
                alertDialogBuilder.setTitle(R.string.dialog_test_result).setMessage(viewPagerAdapter.getTestResult())

                alertDialogBuilder.setPositiveButton(R.string.common_go_main) { dialog, _ ->
                    _mActivity.startNavigationDrawer()
                    _mActivity.changeFragment(R.id.nav_main)
                    _mActivity.supportActionBar!!.show()
                    dialog.dismiss()
                }

                alertDialogBuilder.setNegativeButton(R.string.common_stay) { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
        binding.btnMemorizedSuccess.setOnClickListener{
            val viewPagerAdapter = (viewPager.adapter as MemorizationTestAdapter)
            viewPagerAdapter.setCurrentCardMemorized(viewPager.currentItem, true, memorizationTestViewModel.cardCenterIndex!!)
            val nextIndex = viewPager.currentItem + 1
            if (nextIndex <= viewPagerAdapter.getMaxIndex()) {
                isFromOtherEvent = true
                lastPosition = viewPager.currentItem + 1
                memorizationTestViewModel.cardCenterIndex = viewPagerAdapter.addNextItem(viewPager.currentItem + 1, memorizationTestViewModel.cardCenterIndex!!)

                // 도저히 이방법 말고는 못찾음
                // 딜레이 안주면 정상적으로 작동하지 않는경우가 있음
                // 내 예상은 아이템이 정렬하기 전에 setCurrentItem이 먼저 실행되는거 같음
                val delayMillis = 200 // 0.2 초
                Handler().postDelayed({
                    viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                }, delayMillis.toLong())
            } else {
                val alertDialogBuilder = AlertDialog.Builder(_mActivity)
                alertDialogBuilder.setTitle(R.string.dialog_test_result).setMessage(viewPagerAdapter.getTestResult())

                alertDialogBuilder.setPositiveButton(R.string.common_go_main) { dialog, _ ->
                    _mActivity.startNavigationDrawer()
                    _mActivity.changeFragment(R.id.nav_main)
                    _mActivity.supportActionBar!!.show()
                    dialog.dismiss()
                }

                alertDialogBuilder.setNegativeButton(R.string.common_stay) { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    private fun setAdapterNewItems(viewPager :ViewPager2, cardIndex: Int) {
        val viewPagerAdapter = (viewPager.adapter as MemorizationTestAdapter)
        memorizationTestViewModel.cardCenterIndex = viewPagerAdapter.getCardCenter(cardIndex)
        val index = viewPagerAdapter.setTargetCardItems(cardIndex)

        // 도저히 이방법 말고는 못찾음
        // 딜레이 안주면 정상적으로 작동하지 않는경우가 있음
        // 내 예상은 아이템이 정렬하기 전에 setCurrentItem이 먼저 실행되는거 같음
        val delayMillis = 200 // 0.2 초
        Handler().postDelayed({
            viewPager.setCurrentItem(index, true)
        }, delayMillis.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        memorizationTestViewModel.cardIdAndCardTableIdList = mutableListOf()
        memorizationTestViewModel.cardCenterIndex = null
        memorizationTestViewModel.cardIndex = null
        memorizationTestViewModel.position = null
    }
}