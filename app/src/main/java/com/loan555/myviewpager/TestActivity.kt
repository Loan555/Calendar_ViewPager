package com.loan555.myviewpager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import com.loan555.myviewpager.model.NoteReaderDbHelper
import java.util.*

class TestActivity : AppCompatActivity() {

    private var list = DataNoteList(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        list.addItem(DataNoteItem(0, CalendarDateModel(Calendar.getInstance().time,true),"aaa","bbbbbbbbbbbb"))
        list.addItem(DataNoteItem(0, CalendarDateModel(Calendar.getInstance().time,true),"aaa","bbbbbbbbbbbb"))
        list.readToList()
    }

}