package com.example.memorizationapp.common.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.memorizationapp.common.fileHellper.Node
import com.example.memorizationapp.model.Data

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "memorization.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your tables here
        db.execSQL(DB.SQL_CREATE_TABLE_MainFolder)
        db.execSQL(DB.SQL_CREATE_TABLE_SubFolder)
        db.execSQL(DB.SQL_CREATE_TABLE_Card)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle upgrades (if needed)
    }

    private fun getFolderTree() {
        val db = this.readableDatabase
        val list = listOf<Node<Data>>()
        try {
            val cursor = db.query(
                DB.MainFolder.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            var sub_cursor: Cursor
            var card_cursor: Cursor
            var name: String
            var id: String
            var item: Node<Data>
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(DB.MainFolder.COLUMN_ID) as Int)
                name = cursor.getString(cursor.getColumnIndex(DB.MainFolder.COLUMN_NAME) as Int)
                item = Node(Data.Directory(name))
                sub_cursor = db.query(
                    DB.SubFolder.TABLE_NAME,
                    null,
                    "${DB.SubFolder.COLUMN_MAIN_ID} = ?",
                    arrayOf(id),
                    null,
                    null,
                    null
                )
                while(sub_cursor.moveToNext()) {
                    id = cursor.getString(cursor.getColumnIndex(DB.SubFolder.COLUMN_ID) as Int)
                    name = cursor.getString(cursor.getColumnIndex(DB.SubFolder.COLUMN_NAME) as Int)
                    card_cursor = db.query(
                        DB.Card.TABLE_NAME,
                        null,
                        "${DB.SubFolder.COLUMN_MAIN_ID} = ?",
                        arrayOf(id),
                        null,
                        null,
                        null
                    )
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            db.close()
        }

    }
}