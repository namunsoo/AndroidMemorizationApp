package com.example.memorizationapp.ui.cardList

import android.app.AlertDialog
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper

import com.example.memorizationapp.databinding.ItemCardListBinding
import com.example.memorizationapp.ui.card.CardViewModel

class CardListAdapter(
    private val _mActivity: MainActivity,
    private val cards: MutableList<CardItem>
) : RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    private lateinit var cardViewModel: CardViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {

        cardViewModel = ViewModelProvider(_mActivity)[CardViewModel::class.java]

        return CardViewHolder(
            ItemCardListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(
        holder: CardViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val buttonSetting = holder.itemView.findViewById<ImageButton>(R.id.btn_card_setting)
        val buttonUpdate = holder.itemView.findViewById<ImageButton>(R.id.btn_card_update)
        val buttonDelete = holder.itemView.findViewById<ImageButton>(R.id.btn_card_delete)
        buttonSetting.setOnClickListener {
            if (buttonUpdate.visibility == View.VISIBLE && buttonDelete.visibility == View.VISIBLE) {
                val fadeOut = AlphaAnimation(1f, 0f)
                fadeOut.duration = 400
                buttonUpdate.startAnimation(fadeOut)
                buttonUpdate.visibility = View.GONE
                Handler().postDelayed({
                    buttonDelete.startAnimation(fadeOut)
                    buttonDelete.visibility = View.GONE
                }, 500)

            } else {
                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.duration = 400
                buttonDelete.visibility = View.VISIBLE
                buttonDelete.startAnimation(fadeIn)
                Handler().postDelayed({
                    buttonUpdate.visibility = View.VISIBLE
                    buttonUpdate.startAnimation(fadeIn)
                }, 500)
            }
        }

        val data = cards[position]
        buttonUpdate.setOnClickListener {
            cardViewModel.setValue(data.row, data.id, data.cardBundleId, data.question, data.answer, data.memorized, UPDATE)
            _mActivity.changeFragment(R.id.nav_card)
        }
        buttonDelete.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(_mActivity)
            alertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_card_delete)

            alertDialogBuilder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
                val dbHelper = DBHelper(_mActivity)
                dbHelper.deleteCard(data.id, data.cardBundleId)
                dbHelper.close()
                val index = cards.indexOfFirst { it.row == data.row }
                cards.removeAt(index)
                notifyItemRangeRemoved(index, 1)
                dialog.dismiss()
            }

            alertDialogBuilder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun addItems(items: List<CardItem>) {
        items.forEach {cardItem ->
            cards.add(cardItem)
        }
        notifyItemRangeInserted(cards.size, items.size)
    }

    fun addItemsInFront(items: List<CardItem>) {
        val reverseItem = items.reversed()
        reverseItem.forEach {cardItem ->
            cards.add(0, cardItem)
        }
        notifyItemRangeInserted(0, items.size)
    }

    fun deleteItemsFromFront(deleteItemCount: Int) {
        repeat(deleteItemCount) {
            cards.removeAt(0) // Remove the first item repeatedly
        }
        notifyItemRangeRemoved(0, deleteItemCount)
    }

    fun deleteItemsFromLast(deleteItemCount: Int) {
        val lastIndex = cards.size - 1
        for (i in 0 until deleteItemCount) {
            cards.removeAt(lastIndex - i)
        }
        notifyItemRangeRemoved(lastIndex - deleteItemCount, deleteItemCount)
    }

    fun getCards(): MutableList<CardItem> {
        return cards
    }

    fun updateItem(item: CardItem) {
        val index = cards.indexOfFirst { it.row == item.row }
        cards[index] = item
        notifyItemChanged(index, item)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = cards[position]
        holder.question.text = item.question
        holder.answer.text = item.answer
    }

    override fun getItemCount(): Int = cards.size

    inner class CardViewHolder(binding: ItemCardListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val question: TextView = binding.cardQuestion
        val answer: TextView = binding.cardAnswer
    }

    companion object {
        private const val CREATE = "create"
        private const val UPDATE = "update"
    }

}