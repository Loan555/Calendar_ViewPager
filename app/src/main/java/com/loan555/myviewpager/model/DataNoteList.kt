package com.loan555.myviewpager.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class DataNoteList(var resource: String) {

    private var dataList = ArrayList<DataNoteItem>()

    fun getSize(): Int = dataList.size

    @RequiresApi(Build.VERSION_CODES.N)
    fun sortByDateDescending() {
        this.dataList.sortWith(
            compareBy<DataNoteItem> { it.date.data.year }.reversed()
                .thenBy { it.date.data.month }.reversed()
                .thenBy { it.date.data.date }.reversed()
        )

    }

    fun sortByName() {
        this.dataList.sortBy {
            it.titleHead
        }
    }

    fun addItem(dataNoteItem: DataNoteItem) {
        dataList.add(dataNoteItem)
    }

    fun getNewId(): Int {
        val dataNoteItem = dataList.maxByOrNull {
            it.noteId
        }
        return if (dataNoteItem == null)
            0
        else (dataNoteItem.noteId + 1)
    }

    fun editItem(newNoteItem: DataNoteItem) {
        for (i in 0 until dataList.size) {
            if (newNoteItem.noteId == dataList[i].noteId) {
                dataList[i] = newNoteItem
                return
            }
        }
    }

    fun deleteItem(id: Int) {
        dataList.forEach {
            if (it.noteId == id) {
                dataList.remove(it)
                return
            }
        }
    }

    fun getItem(position: Int): DataNoteItem = dataList[position]

    fun listToString(): String {
        var str = ""
        for (i in 0 until dataList.size) {
            str += "item $i = ${SimpleDateFormat("dd MMMM yyyy").format(dataList[i].date.data)} --- ${dataList[i].titleHead} ---- ${dataList[i].titleBody}\n"
        }
        return str
    }
}