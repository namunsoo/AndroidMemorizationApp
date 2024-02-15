package com.example.memorizationapp.ui.memorizationTest

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel

class MemorizationTestAdapter(private val _mActivity: MainActivity, private val progressTextView: TextView, private val progressSeeBar: SeekBar, private val progressText: String) : RecyclerView.Adapter<MemorizationTestAdapter.ViewHolder>() {
    private val memorizeOptionViewModel = ViewModelProvider(_mActivity)[MemorizeOptionViewModel::class.java]
    private val itemList: MutableList<MemorizationTestCard> = mutableListOf()
    private var itemCount = 0
    private val cardIdAndCardTableIdList: Array<MemorizationTestCardId>
    private var cardCount = 0

    init {
        val dbHelper = DBHelper(_mActivity)
        cardIdAndCardTableIdList = dbHelper.readMemorizationTestCard(
            memorizeOptionViewModel.cardTableIdList.value!!,
            memorizeOptionViewModel.cardType.value!!,
            memorizeOptionViewModel.cardSequence.value!!)
        cardCount = cardIdAndCardTableIdList.count()
        // 페이지 5개로 재사용? 하기위해서 itemList를 5개만 가져오고 그때그때 추가
        if (cardCount >= 5) {
            for (i: Int in 0 until 5) {
                itemList.add(dbHelper.readMemorizationTestCardItem(cardIdAndCardTableIdList[i].cardId, cardIdAndCardTableIdList[i].cardBundleId))
            }
        } else if (cardCount > 0){
            for (i: Int in 0 until cardCount) {
                itemList.add(dbHelper.readMemorizationTestCardItem(cardIdAndCardTableIdList[i].cardId, cardIdAndCardTableIdList[i].cardBundleId))
            }
        }
        itemCount = itemList.count()

        progressSeeBar.max = cardCount
        progressSeeBar.min = 1
        progressSeeBar.progress = if (itemCount == 0) 0 else 1
        val text = "$progressText 1 / $cardCount"
        progressTextView.text = text
        dbHelper.close()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(_mActivity).inflate(R.layout.item_memorization_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    private var beforePosition = 0

    fun addNextItem(position: Int, cardCenterIndex: Int): Int {
        var centerIndex = cardCenterIndex
        var text: String
        if (position > 2 && centerIndex+3 < cardCount) {
            centerIndex++
            val dbHelper = DBHelper(_mActivity)
            itemList.add(dbHelper.readMemorizationTestCardItem(cardIdAndCardTableIdList[centerIndex+2].cardId, cardIdAndCardTableIdList[centerIndex+2].cardBundleId))
            dbHelper.close()
            itemList.removeAt(0)
            notifyItemRemoved(0)
            notifyItemInserted(itemCount - 1)
            text = "$progressText ${centerIndex+1} / $cardCount"
            progressSeeBar.progress = centerIndex+1
        } else {
            text = "$progressText ${centerIndex+position-1} / $cardCount"
            progressSeeBar.progress = centerIndex+position-1
        }
        progressTextView.text = text
        return centerIndex
    }

    fun addBeforeItem(position: Int, cardCenterIndex: Int): Int {
        var centerIndex = cardCenterIndex
        var text: String
        if (position < 2 && centerIndex-3 >= 0) {
            centerIndex--
            itemList.removeAt(itemCount - 1)
            val dbHelper = DBHelper(_mActivity)
            itemList.add(0, dbHelper.readMemorizationTestCardItem(cardIdAndCardTableIdList[centerIndex-2].cardId, cardIdAndCardTableIdList[centerIndex-2].cardBundleId))
            dbHelper.close()
            notifyItemRemoved(itemCount - 1)
            notifyItemInserted(0)
            text = "$progressText ${centerIndex+1} / $cardCount"
            progressSeeBar.progress = centerIndex+1
        } else {
            text = "$progressText ${centerIndex+position-1} / $cardCount"
            progressSeeBar.progress = centerIndex+position-1
        }
        progressTextView.text = text
        return centerIndex
    }

    fun setTargetCardItems(cardIndex: Int): Int {
        var index = cardIndex - 1
        if (cardCount >= 5) {
            notifyItemRangeRemoved(0, itemCount)
            itemList.clear()
            val dbHelper = DBHelper(_mActivity)

            val start: Int
            if (cardIndex > cardCount - 2) {
                start = cardCount - itemCount
                index = (cardIndex - (cardCount % itemCount + 1)) % itemCount
            } else if (cardIndex < 3) {
                start = 0
                index = cardIndex - 1
            } else {
                start = cardIndex - 3
                index = 2
            }

            for (i: Int in 0 until itemCount) {
                itemList.add(dbHelper.readMemorizationTestCardItem(cardIdAndCardTableIdList[start + i].cardId, cardIdAndCardTableIdList[start + i].cardBundleId))
            }
            dbHelper.close()
        }
        val text = "$progressText $cardIndex / $cardCount"
        progressTextView.text = text
        progressSeeBar.progress = cardIndex
        return index
    }

    fun getCardCenter(cardIndex: Int): Int {
        if (cardCount >= 5) {
            if (cardIndex > cardCount - 2) {
                return cardCount - 3
            } else if (cardIndex < 3) {
                return 2
            } else {
                return cardIndex - 1
            }
        }
        return 2
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.findViewById<TextView>(R.id.card_question).text = itemList[position].question
            itemView.findViewById<TextView>(R.id.card_answer).text = itemList[position].answer
            itemView.findViewById<View>(R.id.card_answer_cover).setOnClickListener {
                if (it.visibility == View.VISIBLE) {
                    it.visibility = View.GONE
                }
            }
            itemView.findViewById<TextView>(R.id.card_answer).setOnClickListener {
                val cover = (it.parent as FrameLayout).getChildAt(1)
                if (cover.visibility == View.GONE) {
                    cover.visibility = View.VISIBLE
                }
            }
        }
    }
}
