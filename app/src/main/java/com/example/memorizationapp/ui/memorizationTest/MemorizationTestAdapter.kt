package com.example.memorizationapp.ui.memorizationTest

import android.app.AlertDialog
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.ui.card.CardViewModel
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel

class MemorizationTestAdapter(private val _mActivity: MainActivity, private val progressTextView: TextView, private val progressSeeBar: SeekBar, private val progressText: String) : RecyclerView.Adapter<MemorizationTestAdapter.ViewHolder>() {
    private val memorizeOptionViewModel = ViewModelProvider(_mActivity)[MemorizeOptionViewModel::class.java]
    private val memorizationTestViewModel = ViewModelProvider(_mActivity)[MemorizationTestViewModel::class.java]
    private val cardViewModel = ViewModelProvider(_mActivity)[CardViewModel::class.java]
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
            notifyItemInserted(itemCount)
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

    fun getMaxIndex(): Int {
        return itemCount - 1
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

    fun setCurrentCardMemorized(position: Int, isMemorized: Boolean, cardCenterIndex: Int) {
        val dbHelper = DBHelper(_mActivity)
        if (isMemorized) {
            dbHelper.updateCard(
                itemList[position].id,
                itemList[position].cardBundleId,
                itemList[position].question,
                itemList[position].answer,
                1
            )
        } else {
            dbHelper.updateCard(
                itemList[position].id,
                itemList[position].cardBundleId,
                itemList[position].question,
                itemList[position].answer,
                0
            )
        }
        dbHelper.close()

        if (cardCount >= 5) {
            cardIdAndCardTableIdList[cardCenterIndex - 2 + position].success = if (isMemorized) 1 else 0
        } else if (cardCount > 0) {
            cardIdAndCardTableIdList[position].success = if (isMemorized) 1 else 0
        }
    }

    fun getTestResult(): String {
        var totalSuccess = 0
        for (item in cardIdAndCardTableIdList) {
            if (item.success == 1) {
                totalSuccess++
            }
        }
        return "$cardCount 중 $totalSuccess 개 암기성공"
    }

    // 확실하지는 않은데 메모리 절약용
    // view holder에서서 정의된 리스너는 제거가 안된다고 하는데
    // (그러니까 리스너가 계속 남아있음)
    // 따로 절약하기 위해서 넣어둠
    // fragment 나갈때는 알아서 제거됨
    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.findViewById<TextView>(R.id.tv_card_question).text = itemList[position].question
            itemView.findViewById<TextView>(R.id.tv_card_answer).text = itemList[position].answer
            itemView.findViewById<TextView>(R.id.tv_card_name).text = itemList[position].cardBundleName
            itemView.findViewById<View>(R.id.card_answer_cover).setOnClickListener {
                if (it.visibility == View.VISIBLE) {
                    it.visibility = View.GONE
                }
            }
            itemView.findViewById<TextView>(R.id.tv_card_answer).setOnClickListener {
                val cover = (it.parent as FrameLayout).getChildAt(1)
                if (cover.visibility == View.GONE) {
                    cover.visibility = View.VISIBLE
                }
            }

            val buttonSetting = itemView.findViewById<ImageButton>(R.id.btn_card_setting)
            val buttonUpdate = itemView.findViewById<ImageButton>(R.id.btn_card_update)
            val buttonDelete = itemView.findViewById<ImageButton>(R.id.btn_card_delete)
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

            val data = itemList[position]
            buttonUpdate.setOnClickListener {
                memorizationTestViewModel.position = position
                cardViewModel.setValue(0, data.id, data.cardBundleId, data.question, data.answer, data.memorized, UPDATE)
                _mActivity.supportActionBar!!.show()
                _mActivity.changeFragment(R.id.nav_card)
            }
            buttonDelete.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(_mActivity)
                alertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_card_delete)

                alertDialogBuilder.setPositiveButton(R.string.common_confirm) { dialog, _ ->
                    dialog.dismiss()
                }

                alertDialogBuilder.setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    companion object {
        private const val UPDATE = "update"
    }
}
