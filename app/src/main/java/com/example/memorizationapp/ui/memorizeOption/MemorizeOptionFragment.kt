package com.example.memorizationapp.ui.memorizeOption

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.marginLeft
import androidx.core.view.setMargins
import androidx.lifecycle.Lifecycle
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model

class MemorizeOptionFragment : Fragment() {

    private lateinit var viewModel: MemorizeOptionViewModel

    private lateinit var _mActivity : MainActivity

    private lateinit var folderSelectDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_memorize_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            // 기존 menu 지우고 check menu 설정
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

        val cardTypeSelect = view.findViewById<ConstraintLayout>(R.id.ll_card_type)
        cardTypeSelect.setOnClickListener{

        }

        val cardSequenceSelect = view.findViewById<ConstraintLayout>(R.id.ll_card_sequence)
        cardSequenceSelect.setOnClickListener{

        }
    }

    private fun setFolderSelectDialog() {
        val dbHelper = DBHelper(_mActivity)
        val models = dbHelper.getFolderTree()
        dbHelper.close()

        val dialogView = layoutInflater.inflate(R.layout.dialog_folder_select, null)
        val linearLayout = dialogView.findViewById<LinearLayout>(R.id.ll_dialog_folder)

        // dp -> pixel
        val dp = 30
        val scale = resources.displayMetrics.density
        val dpToPixel = (dp * scale + 0.5f).toInt()

        setFolderSelectDialogItem(models, linearLayout, dpToPixel)

        val builder = AlertDialog.Builder(_mActivity)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
            // Handle positive button click
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
            // Handle negative button click or dismiss
            dialog.dismiss()
        }
        folderSelectDialog = builder.create()
    }

    private fun setFolderSelectDialogItem(models : MutableList<Model<Item>>, linearLayout: LinearLayout, distinguishedSize: Int) {
        for (model in models) {
            val itemView = layoutInflater.inflate(R.layout.item_card_check, null)
            itemView.findViewById<ImageView>(R.id.iv_icon).setPadding(distinguishedSize * model.depth,0,0,0)
            val ctv = itemView.findViewById<CheckedTextView>(R.id.ckd_item_check)
            ctv.text = model.content.name
            itemView.setOnClickListener {
                ctv.isChecked = !ctv.isChecked
                if(ctv.isChecked){
                    itemView.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.basics_base_level1))
                } else {
                    itemView.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white))
                }
            }

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
                is Item.Card -> {
                    itemView.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.ic_file)
                    linearLayout.addView(itemView)
                }
                else -> {}
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MemorizeOptionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}