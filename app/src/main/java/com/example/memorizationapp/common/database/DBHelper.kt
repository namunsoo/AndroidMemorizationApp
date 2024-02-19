package com.example.memorizationapp.common.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.memorizationapp.common.treeRecyclerView.Item
import com.example.memorizationapp.common.treeRecyclerView.Model
import com.example.memorizationapp.ui.cardList.CardItem
import com.example.memorizationapp.ui.memorizationTest.MemorizationTestCard
import com.example.memorizationapp.ui.memorizationTest.MemorizationTestCardId

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "memorization.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your tables here
        db.execSQL(DB.SQL_CREATE_TABLE_MainFolder)
        db.execSQL(DB.SQL_CREATE_TABLE_SubFolder)
        db.execSQL(DB.SQL_CREATE_TABLE_CardBundle)
    }

    fun createNewCardTable(id: Int) {
        val createTableSql = DB.getSqlCreateCard(id)
        val db = this.writableDatabase
        db.execSQL(createTableSql)
    }

    fun getProgressionDegree(): Array<Int> {
        val dbRead = this.readableDatabase
        var cardTableNameIdList = listOf<Int>()
        var cursor: Cursor
        var cardBundleId: Int

        var memorized = 0
        var count = 0

        cursor = dbRead.rawQuery(
            "SELECT ${DB.CARD_BUNDLE.COLUMN_ID} FROM ${DB.CARD_BUNDLE.TABLE_NAME}",null
        )
        while (cursor.moveToNext()) {
            cardBundleId = cursor.getInt(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
            cardTableNameIdList = cardTableNameIdList + listOf(cardBundleId)
        }

        for (id in cardTableNameIdList) {
            cursor = dbRead.query(
                "${DB.CARD.TABLE_NAME}${id}",
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                count++
                if (cursor.getInt(cursor.getColumnIndex(DB.CARD.COLUMN_MEMORIZED) as Int) == 1) {
                    memorized++
                }
            }
        }
        dbRead.close()
        return arrayOf(count, memorized, count - memorized)
    }

    fun getFolderTree(): MutableList<Model<Item>> {
        val db = this.readableDatabase
        val mainCursor = db.rawQuery(
            "SELECT * FROM ${DB.MAIN_FOLDER.TABLE_NAME}",null
        )

        var subCursor : Cursor
        var cardBundleCursor : Cursor
        var mainId: Int
        var subId: Int
        var cardId: Int
        var mainName: String
        var subName: String
        var cardName: String
        var main: Model<Item>
        var sub: Model<Item>
        var card: Model<Item>
        val result: MutableList<Model<Item>> = mutableListOf()
        while (mainCursor.moveToNext()) {
            mainId = mainCursor.getInt(mainCursor.getColumnIndex(DB.MAIN_FOLDER.COLUMN_ID) as Int)
            mainName = mainCursor.getString(mainCursor.getColumnIndex(DB.MAIN_FOLDER.COLUMN_NAME) as Int)
            main = Model(Item.MainFolder(mainId, mainName))
            subCursor = db.query(
                DB.SUB_FOLDER.TABLE_NAME,   // The table to query
                null,  // The columns to return
                "${DB.SUB_FOLDER.COLUMN_MAIN_ID} = ?",   // The columns for the WHERE clause
                arrayOf(mainId.toString()),  // The values for the WHERE clause
                null,        // don't group the rows
                null,        // don't filter by row groups
                null         // don't sort order
            )
            while (subCursor.moveToNext()) {
                subId = subCursor.getInt(subCursor.getColumnIndex(DB.SUB_FOLDER.COLUMN_ID) as Int)
                subName = subCursor.getString(subCursor.getColumnIndex(DB.SUB_FOLDER.COLUMN_NAME) as Int)
                sub = Model(Item.SubFolder(subId, subName, mainId))
                cardBundleCursor = getCardBundleCursor(mainId, subId, db)
                while (cardBundleCursor.moveToNext()) {
                    cardId = cardBundleCursor.getInt(cardBundleCursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
                    cardName = cardBundleCursor.getString(cardBundleCursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_NAME) as Int)
                    card = Model(Item.CardBundle(cardId, cardName, mainId, subId))
                    sub.addChild(card)
                }
                main.addChild(sub)
            }
            cardBundleCursor = getCardBundleCursor(mainId, null, db)
            while (cardBundleCursor.moveToNext()) {
                cardId = cardBundleCursor.getInt(cardBundleCursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
                cardName = cardBundleCursor.getString(cardBundleCursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_NAME) as Int)
                card = Model(Item.CardBundle(cardId, cardName, mainId, null))
                main.addChild(card)
            }
            result.add(main)
        }
        cardBundleCursor = getCardBundleCursor(null, null, db)
        while (cardBundleCursor.moveToNext()) {
            cardId = cardBundleCursor.getInt(cardBundleCursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
            cardName = cardBundleCursor.getString(cardBundleCursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_NAME) as Int)
            card = Model(Item.CardBundle(cardId, cardName, null, null))
            result.add(card)
        }
        db.close()
        return result
    }

    private fun getCardBundleCursor(mainId: Int?, subId: Int?, db: SQLiteDatabase): Cursor {
        var selection: String? = null
        var selectionArgs: Array<String?>? = null
        if (mainId != null && subId != null){
            selection = "${DB.CARD_BUNDLE.COLUMN_MAIN_ID} = ? AND ${DB.CARD_BUNDLE.COLUMN_SUB_ID} = ?"
            selectionArgs = arrayOf(mainId.toString(), subId.toString())
        } else if (mainId != null && subId == null) {
            selection = "${DB.CARD_BUNDLE.COLUMN_MAIN_ID} = ? AND ${DB.CARD_BUNDLE.COLUMN_SUB_ID} IS NULL"
            selectionArgs = arrayOf(mainId.toString())
        } else {
            selection = "${DB.CARD_BUNDLE.COLUMN_MAIN_ID} IS NULL"
        }
        return db.query(
            DB.CARD_BUNDLE.TABLE_NAME,  // table name
            null,     // columns to return
            selection,   // columns for the WHERE clause
            selectionArgs,  // values for the WHERE clause
            null,        // don't group the rows
            null,        // don't filter by row groups
            null         // the sort order
        )
    }

    fun insertMainFolder(name: String): Model<Item> {
        val dbWrite = this.writableDatabase
        val dbRead = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.MAIN_FOLDER.COLUMN_NAME, name)
        dbWrite.insert(DB.MAIN_FOLDER.TABLE_NAME, null, contentValues)
        val cursor = dbRead.rawQuery(
            "SELECT * FROM ${DB.MAIN_FOLDER.TABLE_NAME}",null
        )
        cursor.moveToLast()
        val mainId = cursor.getInt(cursor.getColumnIndex(DB.MAIN_FOLDER.COLUMN_ID) as Int)
        val mainName = cursor.getString(cursor.getColumnIndex(DB.MAIN_FOLDER.COLUMN_NAME) as Int)
        val model: Model<Item> = Model(Item.MainFolder(mainId, mainName))
        dbWrite.close()
        dbRead.close()
        return model
    }

    fun updateMainFolder(model: Model<Item>, name: String) {
        val dbWrite = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.MAIN_FOLDER.COLUMN_NAME, name)
        dbWrite.update(DB.MAIN_FOLDER.TABLE_NAME,
            contentValues,
            "${DB.MAIN_FOLDER.COLUMN_ID} = ?",
            arrayOf(model.content.id.toString()))
        dbWrite.close()
    }

    fun deleteMainFolder(id: Int) {
        val dbWrite = this.writableDatabase
        dbWrite.delete(DB.MAIN_FOLDER.TABLE_NAME,
            "${DB.MAIN_FOLDER.COLUMN_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.close()
    }

    fun insertSubFolder(name: String, mainId: Int): Model<Item> {
        val dbWrite = this.writableDatabase
        val dbRead = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.SUB_FOLDER.COLUMN_NAME, name)
        contentValues.put(DB.SUB_FOLDER.COLUMN_MAIN_ID, mainId)
        dbWrite.insert(DB.SUB_FOLDER.TABLE_NAME, null, contentValues)
        val cursor = dbRead.rawQuery(
            "SELECT * FROM ${DB.SUB_FOLDER.TABLE_NAME}",null
        )
        cursor.moveToLast()
        val subId = cursor.getInt(cursor.getColumnIndex(DB.SUB_FOLDER.COLUMN_ID) as Int)
        val subName = cursor.getString(cursor.getColumnIndex(DB.SUB_FOLDER.COLUMN_NAME) as Int)
        val mainId = cursor.getInt(cursor.getColumnIndex(DB.SUB_FOLDER.COLUMN_MAIN_ID) as Int)
        val model: Model<Item> = Model(Item.SubFolder(subId, subName, mainId))
        dbWrite.close()
        dbRead.close()
        return model
    }

    fun updateSubFolder(model: Model<Item>, name: String) {
        val dbWrite = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.SUB_FOLDER.COLUMN_NAME, name)
        dbWrite.update(DB.SUB_FOLDER.TABLE_NAME,
            contentValues,
            "${DB.SUB_FOLDER.COLUMN_ID} = ?",
            arrayOf(model.content.id.toString()))
        dbWrite.close()
    }

    fun deleteSubFolder(id: Int) {
        val dbWrite = this.writableDatabase
        dbWrite.delete(DB.SUB_FOLDER.TABLE_NAME,
            "${DB.SUB_FOLDER.COLUMN_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.close()
    }

    fun deleteSubFolders(models: List<Model<Item>>) {
        val dbWrite = this.writableDatabase
        for (item in models) {
            dbWrite.delete(DB.SUB_FOLDER.TABLE_NAME,
                "${DB.SUB_FOLDER.COLUMN_ID} = ?",
                arrayOf(item.content.id.toString()))
        }
        dbWrite.close()
    }

    fun insertCardBundle(name: String, mainId: Int? = null, subId: Int? = null): Model<Item> {
        val dbWrite = this.writableDatabase
        val dbRead = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.CARD_BUNDLE.COLUMN_NAME, name)
        if (mainId != null) contentValues.put(DB.CARD_BUNDLE.COLUMN_MAIN_ID, mainId)
        if (subId != null) contentValues.put(DB.CARD_BUNDLE.COLUMN_SUB_ID, subId)
        dbWrite.insert(DB.CARD_BUNDLE.TABLE_NAME, null, contentValues)
        val cursor = dbRead.rawQuery(
            "SELECT * FROM ${DB.CARD_BUNDLE.TABLE_NAME}",null
        )
        cursor.moveToLast()
        val cardBundleId = cursor.getInt(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
        val cardBundleName = cursor.getString(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_NAME) as Int)
        val cardBundleMainId = cursor.getInt(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_MAIN_ID) as Int)
        val cardBundleSubId = cursor.getInt(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_SUB_ID) as Int)
        val model: Model<Item> = Model(Item.CardBundle(cardBundleId, cardBundleName, cardBundleMainId, cardBundleSubId))
        dbWrite.close()
        dbRead.close()
        return model
    }

    fun updateCardBundle(model: Model<Item>, name: String) {
        val dbWrite = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.CARD_BUNDLE.COLUMN_NAME, name)
        dbWrite.update(DB.CARD_BUNDLE.TABLE_NAME,
            contentValues,
            "${DB.CARD_BUNDLE.COLUMN_ID} = ?",
            arrayOf(model.content.id.toString()))
        dbWrite.close()
    }

    fun deleteCardBundle(id: Int){
        val dbWrite = this.writableDatabase
        dbWrite.delete(DB.CARD_BUNDLE.TABLE_NAME,
            "${DB.CARD_BUNDLE.COLUMN_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.execSQL("DROP TABLE IF EXISTS ${DB.CARD.TABLE_NAME}${id}")
        dbWrite.close()
    }

    fun deleteCardBundleWithMainFolderId(id: Int){
        val dbWrite = this.writableDatabase
        val dbRead = this.readableDatabase
        val cursor = dbRead.query(
            DB.CARD_BUNDLE.TABLE_NAME,   // The table to query
            arrayOf(DB.CARD_BUNDLE.COLUMN_ID),  // The columns to return
            "${DB.CARD_BUNDLE.COLUMN_MAIN_ID} = ?",   // The columns for the WHERE clause
            arrayOf(id.toString()),  // The values for the WHERE clause
            null,        // don't group the rows
            null,        // don't filter by row groups
            null         // don't sort order
        )
        var bundleId: String
        while (cursor.moveToNext()) {
            bundleId = cursor.getString(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
            dbWrite.execSQL("DROP TABLE IF EXISTS ${DB.CARD.TABLE_NAME}${bundleId}")
        }
        dbWrite.delete(DB.CARD_BUNDLE.TABLE_NAME,
            "${DB.CARD_BUNDLE.COLUMN_MAIN_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.close()
        dbRead.close()
    }

    fun deleteCardBundleWithSubFolderId(id: Int){
        val dbWrite = this.writableDatabase
        val dbRead = this.readableDatabase
        val cursor = dbRead.query(
            DB.CARD_BUNDLE.TABLE_NAME,   // The table to query
            arrayOf(DB.CARD_BUNDLE.COLUMN_ID),  // The columns to return
            "${DB.CARD_BUNDLE.COLUMN_SUB_ID} = ?",   // The columns for the WHERE clause
            arrayOf(id.toString()),  // The values for the WHERE clause
            null,        // don't group the rows
            null,        // don't filter by row groups
            null         // don't sort order
        )
        var bundleId: String
        while (cursor.moveToNext()) {
            bundleId = cursor.getString(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
            dbWrite.execSQL("DROP TABLE IF EXISTS ${DB.CARD.TABLE_NAME}${bundleId}")
        }
        dbWrite.delete(DB.CARD_BUNDLE.TABLE_NAME,
            "${DB.CARD_BUNDLE.COLUMN_SUB_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.close()
        dbRead.close()
    }

    fun readCard(id: Int, startRow: Int, howMany: Int): MutableList<CardItem> {
        val dbRead = this.readableDatabase
        val cursor = dbRead.query(
            "${DB.CARD.TABLE_NAME}${id}", null, null, null, null, null, "${DB.CARD.COLUMN_ID} DESC", "$startRow, $howMany"
        )
        var count = 1
        var id: Int
        var cardBundleId: Int
        var question: String
        var answer: String
        var memorized: Int
        var item: CardItem
        var cardList: MutableList<CardItem> = mutableListOf()
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(DB.CARD.COLUMN_ID) as Int)
            cardBundleId = cursor.getInt(cursor.getColumnIndex(DB.CARD.CARD_BUNDLE_ID) as Int)
            question = cursor.getString(cursor.getColumnIndex(DB.CARD.COLUMN_QUESTION) as Int)
            answer = cursor.getString(cursor.getColumnIndex(DB.CARD.COLUMN_ANSWER) as Int)
            memorized = cursor.getInt(cursor.getColumnIndex(DB.CARD.COLUMN_MEMORIZED) as Int)
            item = CardItem(startRow + count, id, cardBundleId, question, answer, memorized)
            cardList.add(item)
            count += 1
        }
        dbRead.close()
        return cardList
    }
    fun insertCard(cardBundleId: Int, question: String, answer: String, memorized: Int) {
        val dbWrite = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.CARD.CARD_BUNDLE_ID, cardBundleId)
        contentValues.put(DB.CARD.COLUMN_QUESTION, question)
        contentValues.put(DB.CARD.COLUMN_ANSWER, answer)
        contentValues.put(DB.CARD.COLUMN_MEMORIZED, memorized)
        dbWrite.insert("${DB.CARD.TABLE_NAME}${cardBundleId}", null, contentValues)
        dbWrite.close()
    }

    fun updateCard(id: Int, cardBundleId: Int, question: String, answer: String, memorized: Int? = null) {
        val dbWrite = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DB.CARD.COLUMN_QUESTION, question)
        contentValues.put(DB.CARD.COLUMN_ANSWER, answer)
        if(memorized != null) {
            contentValues.put(DB.CARD.COLUMN_MEMORIZED, memorized)
        }

        dbWrite.update("${DB.CARD.TABLE_NAME}${cardBundleId}",
            contentValues,
            "${DB.CARD.COLUMN_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.close()
    }

    fun deleteCard(id: Int, cardBundleId: Int) {
        val dbWrite = this.writableDatabase
        dbWrite.delete("${DB.CARD.TABLE_NAME}${cardBundleId}",
            "${DB.CARD.COLUMN_ID} = ?",
            arrayOf(id.toString()))
        dbWrite.close()
    }

    fun readMemorizationTestCard(idList: List<Int>, cardType: String, cardSequence: String): MutableList<MemorizationTestCardId> {
        val dbRead = this.readableDatabase
        var cardTableNameIdList = listOf<Int>()
        var mutableList: MutableList<MemorizationTestCardId> = mutableListOf()
        var cursor: Cursor
        var cardId: Int
        var cardBundleId: Int

        if (idList.isEmpty()) {

            cursor = dbRead.rawQuery(
                "SELECT ${DB.CARD_BUNDLE.COLUMN_ID} FROM ${DB.CARD_BUNDLE.TABLE_NAME}",null
            )
            while (cursor.moveToNext()) {
                cardBundleId = cursor.getInt(cursor.getColumnIndex(DB.CARD_BUNDLE.COLUMN_ID) as Int)
                cardTableNameIdList = cardTableNameIdList + listOf(cardBundleId)
            }
        } else {
            cardTableNameIdList = idList
        }

        for (id in cardTableNameIdList) {
            cursor = dbRead.query(
                "${DB.CARD.TABLE_NAME}${id}",
                null,
                if (cardType == "all") null else "${DB.CARD.COLUMN_MEMORIZED} = ?",
                if (cardType == "all") null else
                    if (cardType == "memorized") arrayOf("1") else arrayOf("0"),
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                cardId = cursor.getInt(cursor.getColumnIndex(DB.CARD.COLUMN_ID) as Int)
                cardBundleId = cursor.getInt(cursor.getColumnIndex(DB.CARD.CARD_BUNDLE_ID) as Int)
                mutableList.add(MemorizationTestCardId(cardId, cardBundleId, 0))
            }
        }
        dbRead.close()

        if (cardSequence == "random") {
            mutableList.shuffle()
        }
        return mutableList
    }

    fun readMemorizationTestCardItem(cardId: Int, cardBundleId: Int): MemorizationTestCard {
        val dbRead = this.readableDatabase
        val cursorCard = dbRead.query(
            "${DB.CARD.TABLE_NAME}${cardBundleId}",
            null,
            "${DB.CARD.COLUMN_ID} = ?",
            arrayOf(cardId.toString()),
            null,
            null,
            null,
            null
        )
        val cursorCardBundle = dbRead.query(
            "${DB.CARD_BUNDLE.TABLE_NAME}",
            null,
            "${DB.CARD_BUNDLE.COLUMN_ID} = ?",
            arrayOf(cardBundleId.toString()),
            null,
            null,
            null,
            null
        )
        cursorCard.moveToLast()
        cursorCardBundle.moveToLast()
        val id = cursorCard.getInt(cursorCard.getColumnIndex(DB.CARD.COLUMN_ID) as Int)
        val cardBundleId = cursorCard.getInt(cursorCard.getColumnIndex(DB.CARD.CARD_BUNDLE_ID) as Int)
        val cardBundleName = cursorCardBundle.getString(cursorCardBundle.getColumnIndex(DB.CARD_BUNDLE.COLUMN_NAME) as Int)
        val question = cursorCard.getString(cursorCard.getColumnIndex(DB.CARD.COLUMN_QUESTION) as Int)
        val answer = cursorCard.getString(cursorCard.getColumnIndex(DB.CARD.COLUMN_ANSWER) as Int)
        val memorized = cursorCard.getInt(cursorCard.getColumnIndex(DB.CARD.COLUMN_MEMORIZED) as Int)
        val item = MemorizationTestCard(
            id, cardBundleId, cardBundleName, question, answer, memorized
        )
        dbRead.close()
        return item
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle upgrades (if needed)
        val test = 0
    }
}