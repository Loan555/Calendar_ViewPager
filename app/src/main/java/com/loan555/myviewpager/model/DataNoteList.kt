package com.loan555.myviewpager.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@WorkerThread
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DataNoteList(val context: Context) {
    private var dataList = ArrayList<DataNoteItem>()
    private val dbHelper = NoteReaderDbHelper(context)

    @SuppressLint("SimpleDateFormat")
    fun readToList(): Int {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            NoteReaderContract.NoteEntry.COLUMN_DATE,
            NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE,
            NoteReaderContract.NoteEntry.COLUMN_NAME_SUBTITLE
        )
        val sortOrder = "${NoteReaderContract.NoteEntry.COLUMN_DATE} DESC"
        val cursor = db.query(
            NoteReaderContract.NoteEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )
        var mDataList = ArrayList<DataNoteItem>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val itemDate = CalendarDateModel(
                    SimpleDateFormat("yyyy/MM/dd").parse(
                        getString(getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_DATE))
                    ),true
                )
                val itemTitle =
                    getString(getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE))
                val itemSubTitle =
                    getString(getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_SUBTITLE))
                // doc xong thi add vao list
                val dataItem = DataNoteItem(
                    itemId,
                    itemDate,
                    itemTitle,
                    itemSubTitle
                )
                mDataList.add(dataItem)
            }
        }
        dataList = mDataList
        return dataList.size
    }

    fun setData(newDataNoteList: ArrayList<DataNoteItem>){
        dataList = newDataNoteList
    }

    fun addItem(dataNoteItem: DataNoteItem): Int {
        //neu them thanh cong vao database thi them vao list
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(
                NoteReaderContract.NoteEntry.COLUMN_DATE,
                SimpleDateFormat("yyyy/MM/dd").format(dataNoteItem.date.data)
            )
            put(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE, dataNoteItem.titleHead)
            put(NoteReaderContract.NoteEntry.COLUMN_NAME_SUBTITLE, dataNoteItem.titleBody)
        }
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(NoteReaderContract.NoteEntry.TABLE_NAME, null, values)

        if (newRowId != null) {
            //tim vi tri thich hop de them
            for (i in 0 until dataList.size) {
                if (SimpleDateFormat("yyyy/MM/dd").format(dataNoteItem.date.data) >= SimpleDateFormat(
                        "yyyy/MM/dd"
                    ).format(dataList[i].date.data)
                ) {
                    dataList.add(
                        i,
                        DataNoteItem(
                            newRowId,
                            dataNoteItem.date,
                            dataNoteItem.titleHead,
                            dataNoteItem.titleBody
                        )
                    )
                    return i
                }
            }
            dataList.add(
                DataNoteItem(
                    newRowId,
                    dataNoteItem.date,
                    dataNoteItem.titleHead,
                    dataNoteItem.titleBody
                )
            )
            return dataList.size - 1
            Log.d("ddd", " ban vua them itemId: $newRowId")
        }
        return -1
    }

    fun updateItem(newNoteItem: DataNoteItem): Int {
        // nao ranh viet lai cai nay sau
        var updateRow = -1
        dataList.forEach {
            if (it.noteId == newNoteItem.noteId){
                it.date = newNoteItem.date
                it.titleHead = newNoteItem.titleHead
                it.titleBody = newNoteItem.titleBody
                val db = dbHelper.writableDatabase
                val selection = "${BaseColumns._ID} = ?"
                val selectionArg = arrayOf("${it.noteId}")
                val values = ContentValues().apply {
                    put(NoteReaderContract.NoteEntry.COLUMN_DATE,SimpleDateFormat("yyyy/MM/dd").format(newNoteItem.date.data))
                    put(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE,newNoteItem.titleHead)
                    put(NoteReaderContract.NoteEntry.COLUMN_NAME_SUBTITLE,newNoteItem.titleBody)
                }
                updateRow =
                    db?.update(NoteReaderContract.NoteEntry.TABLE_NAME,values,selection,selectionArg)!!
                Log.e("eee","update item = $it")
                return@forEach
            }
        }
        return updateRow
    }

    fun deleteItem(dataNoteItem: DataNoteItem): Int {
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArg = arrayOf("${dataNoteItem.noteId}")
        val deleteRows =
            db.delete(NoteReaderContract.NoteEntry.TABLE_NAME, selection, selectionArg)
        dataList.remove(dataNoteItem)
        return deleteRows
    }

    fun closeDatabase() {
        dbHelper.close()
    }

    fun getList(): ArrayList<DataNoteItem> = dataList

    fun getItemByID(ids: Long): DataNoteItem{
        var result = DataNoteItem(-1, CalendarDateModel(Calendar.getInstance().time,false),"","")
        dataList.forEach {
            if (it.noteId == ids) {
                result = it
                return@forEach
            }
        }
        return result
    }

    fun getSize(): Int = dataList.size

    fun clear() {
        val db = dbHelper.readableDatabase
        this.dataList.clear()// clear list
        db.delete(NoteReaderContract.NoteEntry.TABLE_NAME,null,null) // clear data base
    }

    fun sortByName() {
        this.dataList.sortBy {
            it.titleHead
        }
    }

    fun getNewId(): Long {
        val dataNoteItem = dataList.maxByOrNull {
            it.noteId
        }
        return if (dataNoteItem == null)
            0
        else (dataNoteItem.noteId + 1)
    }

    fun getItem(position: Int): DataNoteItem = dataList[position]

    fun listToString(): String {
        var str = ""
        for (i in 0 until dataList.size) {
            str += "\n item $i = ${SimpleDateFormat("yyyy/MM/dd").format(dataList[i].date.data)} --- ${dataList[i].titleHead} ---- ${dataList[i].titleBody}\n"
        }
        return str
    }
}