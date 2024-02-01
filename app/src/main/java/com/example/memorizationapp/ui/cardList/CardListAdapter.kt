package com.example.memorizationapp.ui.cardList

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.memorizationapp.R

import com.example.memorizationapp.ui.cardList.placeholder.PlaceholderContent.PlaceholderItem
import com.example.memorizationapp.databinding.ItemCardListBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CardListAdapter(
    private val cards: List<PlaceholderItem>
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

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = cards[position]
        holder.question.text = item.id.toString()
        holder.answer.text = item.question
    }

    override fun getItemCount(): Int = cards.size

    inner class CardViewHolder(binding: ItemCardListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val question: TextView = binding.cardQuestion
        val answer: TextView = binding.cardAnswer
    }

}