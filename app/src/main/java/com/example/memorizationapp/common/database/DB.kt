package com.example.memorizationapp.common.database

import android.provider.BaseColumns

object DB {
    object MainFolder : BaseColumns {
        const val TABLE_NAME = "main_folder"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }
    object SubFolder : BaseColumns {
        const val TABLE_NAME = "sub_folder"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MAIN_ID = "main_id"
    }
    object Card : BaseColumns {
        const val TABLE_NAME = "card"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MAIN_ID = "main_id"
        const val COLUMN_SUB_ID = "sub_id"
    }

    const val SQL_CREATE_TABLE_MainFolder = "CREATE TABLE IF NOT EXISTS ${MainFolder.TABLE_NAME} (" +
            "${MainFolder.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${MainFolder.COLUMN_NAME} TEXT PRIMARY KEY)"

    const val SQL_CREATE_TABLE_SubFolder = "CREATE TABLE IF NOT EXISTS ${SubFolder.TABLE_NAME} (" +
            "${SubFolder.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${SubFolder.COLUMN_NAME} TEXT PRIMARY KEY, " +
            "${SubFolder.COLUMN_MAIN_ID} INTEGER NOT NULL)"

    const val SQL_CREATE_TABLE_Card = "CREATE TABLE IF NOT EXISTS ${Card.TABLE_NAME} (" +
            "${Card.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${Card.COLUMN_NAME} INTEGER NOT NULL, " +
            "${Card.COLUMN_MAIN_ID} INTEGER NOT NULL, " +
            "${Card.COLUMN_SUB_ID} INTEGER NOT NULL)"
}