package com.example.memorizationapp.ui.memorizeOption

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model
import com.example.memorizationapp.ui.main.MainViewModel

class MemorizeOptionFragment : Fragment() {

    private lateinit var memorizeOptionViewModel : MemorizeOptionViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var _mActivity : MainActivity

    private lateinit var folderSelectDialog: AlertDialog
    private lateinit var cardTypeSelectDialog: AlertDialog
    private lateinit var cardSequenceSelectDialog: AlertDialog

    private val _checkedRows: MutableList<Int> = mutableListOf()
    private val _tempCheckedRows: MutableList<Int> = mutableListOf()
    private val _cardTableId: MutableList<Int> = mutableListOf()
    private val _tempCardTableId: MutableList<Int> = mutableListOf()
    private var _cardType: String = MEMORIZING
    private var _cardSequence: String = RANDOM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        memorizeOptionViewModel = ViewModelProvider(requireActivity())[MemorizeOptionViewModel::class.java]
        _mActivity = activity as MainActivity

        memorizeOptionViewModel.setValue(mutableListOf(), MEMORIZING, RANDOM)
        
        return inflater.inflate(R.layout.fragment_memorize_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            // 기존 menu 지우기
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            // menu 클릭 이벤트 설정
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setFolderSelectDialog()
        val cardFolderSelect = view.findViewById<ConstraintLayout>(R.id.ll_folder_choice)
        cardFolderSelect.setOnClickListener{
            folderSelectDialog.show()
        }

        setCardTypeSelectDialog()
        val cardTypeSelect = view.findViewById<ConstraintLayout>(R.id.ll_card_type)
        cardTypeSelect.setOnClickListener{
            cardTypeSelectDialog.show()
        }

        setCardSequenceSelectDialog()
        val cardSequenceSelect = view.findViewById<ConstraintLayout>(R.id.ll_card_sequence)
        cardSequenceSelect.setOnClickListener{
            cardSequenceSelectDialog.show()
        }

        val buttonTestStart = view.findViewById<Button>(R.id.btn_test_start)
        buttonTestStart.setOnClickListener {
            memorizeOptionViewModel.setValue(_cardTableId, _cardType, _cardSequence)
            _mActivity.changeFragment(R.id.nav_memorization_test)
        }
    }

    private fun setCardSequenceSelectDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_card_sequence_select, null)
        val tvBasics = dialogView.findViewById<FrameLayout>(R.id.fl_card_sequence_basics).getChildAt(0)
        val tvRandom = dialogView.findViewById<FrameLayout>(R.id.fl_card_sequence_random).getChildAt(0)
        var tempCardSequence = ALL
        tvBasics.setOnClickListener {
            tvBasics.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
            tvRandom.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tempCardSequence = BASICS
        }
        tvRandom.setOnClickListener {
            tvBasics.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tvRandom.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
            tempCardSequence = RANDOM
        }
        val builder = AlertDialog.Builder(_mActivity)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
            _cardSequence = tempCardSequence
            when (_cardSequence) {
                RANDOM -> {
                    view?.findViewById<TextView>(R.id.tv_card_sequence_result)!!.text = getString(R.string.common_random)
                }
                else -> {
                    view?.findViewById<TextView>(R.id.tv_card_sequence_result)!!.text = getString(R.string.common_basics)
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
            tvBasics.setBackgroundColor(ContextCompat.getColor(
                _mActivity, if (_cardSequence == BASICS) R.color.basics_base_level1 else R.color.white))
            tvRandom.setBackgroundColor(ContextCompat.getColor(
                _mActivity, if (_cardSequence == RANDOM) R.color.basics_base_level1 else R.color.white))
            dialog.dismiss()
        }
        cardSequenceSelectDialog = builder.create()
    }

    private fun setCardTypeSelectDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_card_type_select, null)
        val tvAll = dialogView.findViewById<FrameLayout>(R.id.fl_card_type_all).getChildAt(0)
        val tvMemorizing = dialogView.findViewById<FrameLayout>(R.id.fl_card_type_memorizing).getChildAt(0)
        val tvMemorized = dialogView.findViewById<FrameLayout>(R.id.fl_card_type_memorized).getChildAt(0)
        var tempCardType = ALL
        tvAll.setOnClickListener {
            tvAll.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
            tvMemorizing.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tvMemorized.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tempCardType = ALL
        }
        tvMemorizing.setOnClickListener {
            tvAll.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tvMemorizing.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
            tvMemorized.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tempCardType = MEMORIZING
        }
        tvMemorized.setOnClickListener {
            tvAll.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tvMemorizing.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            tvMemorized.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
            tempCardType = MEMORIZED
        }
        val builder = AlertDialog.Builder(_mActivity)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
            _cardType = tempCardType
            when (_cardType) {
                MEMORIZING -> {
                    view?.findViewById<TextView>(R.id.tv_card_type_result)!!.text = getString(R.string.common_memorizing)
                }
                MEMORIZED -> {
                    view?.findViewById<TextView>(R.id.tv_card_type_result)!!.text = getString(R.string.common_memorized)
                }
                else -> {
                    view?.findViewById<TextView>(R.id.tv_card_type_result)!!.text = getString(R.string.common_all)
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
            tvAll.setBackgroundColor(ContextCompat.getColor(
                _mActivity, if (_cardType == ALL) R.color.basics_base_level1 else R.color.white))
            tvMemorizing.setBackgroundColor(ContextCompat.getColor(
                _mActivity, if (_cardType == MEMORIZING) R.color.basics_base_level1 else R.color.white))
            tvMemorized.setBackgroundColor(ContextCompat.getColor(
                _mActivity, if (_cardType == MEMORIZED) R.color.basics_base_level1 else R.color.white))
            dialog.dismiss()
        }
        cardTypeSelectDialog = builder.create()
    }

    private fun setFolderSelectDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_folder_select, null)
        val linearLayout = dialogView.findViewById<LinearLayout>(R.id.ll_dialog_folder)

        // dp -> pixel
        val dp = 30
        val scale = resources.displayMetrics.density
        val dpToPixel = (dp * scale + 0.5f).toInt()

        setFolderSelectDialogItem(mainViewModel.modelList.value!!, linearLayout, dpToPixel)

        val builder = AlertDialog.Builder(_mActivity)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
            _tempCheckedRows.clear()
            _tempCheckedRows.addAll(_checkedRows)
            _cardTableId.clear()
            _cardTableId.addAll(_tempCardTableId)
            view?.findViewById<TextView>(R.id.tv_file_choice_result)!!.text = if (_cardTableId.isEmpty())
                getString(R.string.common_all) else "${_cardTableId.count()} 개 카드 묶음"
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
            var ctv: View
            for (i in 0 until linearLayout.childCount) {
                ctv = ((linearLayout.getChildAt(i) as ConstraintLayout).getChildAt(0) as ConstraintLayout).getChildAt(1)
                if (ctv is CheckedTextView) {
                    if(_tempCheckedRows.contains(i)){
                        ctv.isChecked = true
                        linearLayout.getChildAt(i).setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
                    } else {
                        ctv.isChecked = false
                        linearLayout.getChildAt(i).setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
                    }
                }
            }
            dialog.dismiss()
        }
        folderSelectDialog = builder.create()
    }

    private var count = 0
    private fun setFolderSelectDialogItem(models : MutableList<Model<Item>>, linearLayout: LinearLayout, distinguishedSize: Int) {
        for (model in models) {
            val itemView = layoutInflater.inflate(R.layout.item_card_check, null)
            itemView.findViewById<ImageView>(R.id.iv_icon).setPadding(distinguishedSize * model.depth,0,0,0)
            val ctv = itemView.findViewById<CheckedTextView>(R.id.ckd_item_check)
            ctv.text = model.content.name
            itemView.id = count
            itemView.setOnClickListener {
                checkItemAndSub(model, linearLayout, it.id, ctv.isChecked)
            }
            count++
            when (model.content) {
                is Item.MainFolder -> {
                    linearLayout.addView(itemView)
                    if (model.haveChildren) {
                        setFolderSelectDialogItem(model.children, linearLayout, distinguishedSize)
                    }
                }
                is Item.SubFolder -> {
                    linearLayout.addView(itemView)
                    if (model.haveChildren) {
                        setFolderSelectDialogItem(model.children, linearLayout, distinguishedSize)
                    }
                }
                is Item.CardBundle -> {
                    itemView.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.ic_file)
                    linearLayout.addView(itemView)
                }
                else -> { }
            }
        }
    }

    // 진짜 배열의 index 기준이 아니라 LinearLayout의 몇번째 행에 CheckTextBox인지
    private fun checkItemAndSub(model: Model<Item>, linearLayout: LinearLayout, index: Int, isChecked: Boolean): Int {
        val ctv = ((linearLayout.getChildAt(index) as ConstraintLayout).getChildAt(0) as ConstraintLayout).getChildAt(1) as CheckedTextView
        ctv.isChecked = !isChecked
        if (ctv.isChecked) {
            _checkedRows.add(index) // LinearLayout의 몇번째 행에 CheckTextBox인지 행 숫자 리스트에 추가
            linearLayout.getChildAt(index).setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
            if (model.content is Item.CardBundle) {
                _tempCardTableId.add(model.content.id)
            }
        } else {
            _checkedRows.remove(index) // LinearLayout의 몇번째 행에 CheckTextBox인지 행 숫자 리스트에서 삭제
            linearLayout.getChildAt(index).setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
            if (model.content is Item.CardBundle) {
                _tempCardTableId.remove(model.content.id)
            }
        }
        var count = index
        if (model.haveChildren) {
            for (item in model.children) {
                count = checkItemAndSub(item, linearLayout, count + 1, isChecked)
            }
        }
        return count
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    companion object {
        private const val ALL = "all"
        private const val MEMORIZING = "memorizing"
        private const val MEMORIZED = "memorized"
        private const val BASICS = "basics"
        private const val RANDOM = "random"
    }
}