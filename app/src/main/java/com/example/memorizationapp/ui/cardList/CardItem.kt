package com.example.memorizationapp.ui.cardList

data class CardItem(val row: Int, val id: Int, val cardBundleId: Int, var question: String, var answer: String, var memorized: Int)