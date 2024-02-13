package com.example.memorizationapp.common.treeRecyclerView

sealed class Item(
    val id: Int,
    var name: String,
    var main_id: Int? = null,
    var sub_id: Int? = null,
) {
    class MainFolder(id: Int, name: String): Item(id, name)
    class SubFolder(id: Int, name: String, main_id: Int): Item(id, name, main_id)
    class CardBundle(id: Int, name: String, main_id: Int?, sub_id: Int?): Item(id, name, main_id, sub_id)
}