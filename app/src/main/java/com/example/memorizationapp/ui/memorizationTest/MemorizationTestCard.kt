package com.example.memorizationapp.ui.memorizationTest

data class MemorizationTestCard(val id: Int, val cardBundleId: Int, val cardBundleName: String, var question: String, var answer: String, var memorized: Int)

data class MemorizationTestCardId(val cardId: Int, val cardBundleId: Int, var success: Int)