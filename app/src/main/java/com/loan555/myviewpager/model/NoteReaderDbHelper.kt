package com.loan555.myviewpager.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object NoteReaderContract {
    // Table contents are grouped together in an anonymous object.
    object NoteEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_DATE = "date"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }
}

private const val SQL_CREATE_ENTRIES = "CREATE TABLE ${NoteReaderContract.NoteEntry.TABLE_NAME}(" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${NoteReaderContract.NoteEntry.COLUMN_DATE} DATE," +
        "${NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE} TEXT," +
        "${NoteReaderContract.NoteEntry.COLUMN_NAME_SUBTITLE} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${NoteReaderContract.NoteEntry.TABLE_NAME}"

class NoteReaderDbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "NoteReader1.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}