package com.example.memorizationapp.common.database

import android.provider.BaseColumns

object DB {
    object MAIN_FOLDER : BaseColumns {
        const val TABLE_NAME = "main_folder"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }
    object SUB_FOLDER : BaseColumns {
        const val TABLE_NAME = "sub_folder"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MAIN_ID = "main_id"
    }
    object CARD_BUNDLE : BaseColumns {
        const val TABLE_NAME = "card_bundle"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MAIN_ID = "main_id"
        const val COLUMN_SUB_ID = "sub_id"
    }
    object CARD : BaseColumns {
        const val TABLE_NAME = "card_item_" // card_bundle id를 붙여서 사용함
        const val COLUMN_ID = "id"
        const val CARD_BUNDLE_ID = "card_bundle_id"
        const val COLUMN_QUESTION = "question"
        const val COLUMN_ANSWER = "answer"
        const val COLUMN_MEMORIZED = "memorized"
    }

    const val SQL_CREATE_TABLE_MainFolder = "CREATE TABLE IF NOT EXISTS ${MAIN_FOLDER.TABLE_NAME} (" +
            "${MAIN_FOLDER.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${MAIN_FOLDER.COLUMN_NAME} TEXT NOT NULL);"

    const val SQL_CREATE_TABLE_SubFolder = "CREATE TABLE IF NOT EXISTS ${SUB_FOLDER.TABLE_NAME} (" +
            "${SUB_FOLDER.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${SUB_FOLDER.COLUMN_NAME} TEXT NOT NULL, " +
            "${SUB_FOLDER.COLUMN_MAIN_ID} INTEGER NOT NULL);"

    const val SQL_CREATE_TABLE_CardBundle = "CREATE TABLE IF NOT EXISTS ${CARD_BUNDLE.TABLE_NAME} (" +
            "${CARD_BUNDLE.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${CARD_BUNDLE.COLUMN_NAME} INTEGER NOT NULL, " +
            "${CARD_BUNDLE.COLUMN_MAIN_ID} INTEGER, " +
            "${CARD_BUNDLE.COLUMN_SUB_ID} INTEGER);"

    fun getSqlCreateCard(cardBundleID: Int): String {
        return "CREATE TABLE IF NOT EXISTS ${CARD.TABLE_NAME}${cardBundleID} (" +
                "${CARD.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${CARD.CARD_BUNDLE_ID} INTEGER NOT NULL, " +
                "${CARD.COLUMN_QUESTION} TEXT NOT NULL, " +
                "${CARD.COLUMN_ANSWER} TEXT NOT NULL, " +
                "${CARD.COLUMN_MEMORIZED} INTEGER NOT NULL);"
    }

}