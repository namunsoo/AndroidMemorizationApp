package com.example.memorizationapp.ui.card

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
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
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.databinding.FragmentCardBinding
import com.example.memorizationapp.ui.cardList.CardListViewModel
import com.example.memorizationapp.ui.folder.FolderFragment

class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardListViewModel: CardListViewModel
    private lateinit var cardViewModel: CardViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardBinding.inflate(inflater, container, false)
        _mActivity = activity as MainActivity
        cardListViewModel = ViewModelProvider(requireActivity())[CardListViewModel::class.java]
        cardViewModel = ViewModelProvider(requireActivity())[CardViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionType = cardViewModel.action.value.toString()
        when (actionType) {
            CREATE -> {
                _mActivity.supportActionBar?.setTitle(R.string.menu_card_create)
            }
            UPDATE -> {
                binding.cardQuestion.setText(cardViewModel.cardQuestion.value)
                binding.cardAnswer.setText(cardViewModel.cardAnswer.value)
                _mActivity.supportActionBar?.setTitle(R.string.menu_card_update)
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
                    R.id.check -> {
                        if (actionType == CREATE) {
                            if(!createCard()){
                                return false
                            } else {
                                binding.cardQuestion.setText("")
                                binding.cardAnswer.setText("")
                            }
                        } else {

                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createCard(): Boolean {
        if (!valueCheck()) return false
        val dbHelper = DBHelper(_mActivity)
        val cardItem = dbHelper.insertCard(
            cardViewModel.cardBundleId.value!!,
            binding.cardQuestion.text.toString(),
            binding.cardAnswer.text.toString(),
            0
        )
        dbHelper.close()
        val builder: AlertDialog.Builder = AlertDialog.Builder(_mActivity)
        builder.setMessage(R.string.dialog_card_insert_complete)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.basics_base_level2)))
        dialog.show()
        cardListViewModel.cardList.value!!.add(0, cardItem)
        val dismissHandler = Handler()
        dismissHandler.postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, 1000)
        return true
    }

    private fun valueCheck(): Boolean {
        val question: String = binding.cardQuestion.text.toString()
        val answer: String = binding.cardAnswer.text.toString()
        val builder: AlertDialog.Builder = AlertDialog.Builder(_mActivity)
        if (question.isEmpty()) {
            builder.setMessage(R.string.dialog_card_question).setTitle(R.string.dialog_title)
            builder.create().show()
            val imm = _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(binding.cardQuestion, 0)
            return false
        }
        if (answer.isEmpty()) {
            builder.setMessage(R.string.dialog_card_answer).setTitle(R.string.dialog_title)
            builder.create().show()
            val imm = _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(binding.cardAnswer, 0)
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