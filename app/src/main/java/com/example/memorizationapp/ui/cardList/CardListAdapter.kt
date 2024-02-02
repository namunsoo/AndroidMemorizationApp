package com.example.memorizationapp.ui.cardList

import android.opengl.Visibility
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.memorizationapp.R

import com.example.memorizationapp.databinding.ItemCardListBinding

class CardListAdapter(
    private val cards: List<CardItem>
) : RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {

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
        holder.itemView.setOnSingleClickListener {
            val button = it.findViewById(R.id.btn_card_setting) as ImageButton
            if (button.visibility == View.VISIBLE) {
                button.visibility = View.GONE
            } else {
                button.visibility = View.VISIBLE
            }
        }

        super.onBindViewHolder(holder, position, payloads)
    }

    private fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
        var lastClickTime = System.currentTimeMillis()

        setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < 500) return@setOnClickListener

            lastClickTime = System.currentTimeMillis()

            onSingleClick(this)
        }
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

}