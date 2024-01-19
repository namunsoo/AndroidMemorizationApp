package com.example.memorizationapp.model

sealed class Data(val name: String) {
    class File(name: String): Data(name)
    class Directory(name: String): Data(name)
}