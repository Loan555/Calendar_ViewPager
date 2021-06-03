package com.loan555.myviewpager.fragment

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.loan555.myviewpager.R
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import kotlinx.android.synthetic.main.fragment_detail.*
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment(var dataNoteList: DataNoteList) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        eventBody()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mymenu, menu)
        menu.findItem(R.id.menu_search).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun eventBody() {
        getDate()
        add()
        text_detail.setOnClickListener { clear() }
    }

    private fun init() {
        val bundle = arguments
        val data = bundle?.get(NOTE) as DataNoteItem
        if (data != null) {
            idItem.text = data.noteId.toString()
            editTextEventName.setText(data.titleHead)
            editTextBody.setText(data.titleBody)
            enterDate.text = SimpleDateFormat("dd/MM/yyyy").format(data.date.data)
        } else
            enterDate.text = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
    }

    private fun add() {
        btn_add.setOnClickListener {
            val date = SimpleDateFormat("dd/MM/yyyy").parse(enterDate.text.toString())
            val name = editTextEventName.text.toString()
            val body = editTextBody.text.toString()
            val id: Long = 0
            Log.d("aaa", "${SimpleDateFormat("dd MM yyyy").format(date)} + $name + $body")

            if (name == "" && body == "") {
                Toast.makeText(this.requireContext(), "Null", Toast.LENGTH_SHORT).show()
            } else {
                val positionAdd = dataNoteList.addItem(
                    DataNoteItem(
                        id,
                        CalendarDateModel(date,true), name, body
                    )
                )
                if (positionAdd != -1)
                    Toast.makeText(this.requireContext(), "Add $positionAdd", Toast.LENGTH_SHORT)
                        .show()
                clear()
            }
        }
    }

    private fun clear() {
        editTextEventName.text.clear()
        editTextBody.text.clear()
        enterDate.text = SimpleDateFormat("dd/MM/YYYY").format(Calendar.getInstance().time)
    }

    private fun getDate() {
        enterDate.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(v: View?) {
                val date = SimpleDateFormat("dd/MM/yyyy").parse(enterDate.text.toString())
                val y = SimpleDateFormat("yyyy").format(date).toInt()
                val m = date.month
                val d = date.date
                Log.d("aaa", "$y/$m/$d")

                var picker = DatePickerDialog(
                    this@DetailFragment.requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        enterDate.text = "$dayOfMonth/${month + 1}/$year"
                    }, y, m, d
                )
                picker.show()
            }
        })
    }
}