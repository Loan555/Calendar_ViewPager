package com.loan555.myviewpager.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loan555.myviewpager.DETAIL_TAG
import com.loan555.myviewpager.R
import com.loan555.myviewpager.adapter.RecyclerNoteAdapter
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import kotlinx.android.synthetic.main.fragment_to_do_list.btn_add
import kotlinx.android.synthetic.main.itempager.*
import java.text.SimpleDateFormat
import java.util.*

class ToDoListFragment(var listData: DataNoteList) : Fragment(),
    RecyclerNoteAdapter.OnItemClickListener {

    var adapter = RecyclerNoteAdapter(listData, this)
    var textSearch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_to_do_list, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        printList()
        initRecyclerView()
//        eventSwipedRecyclerView()
        eventBtnAdd()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mymenu, menu)
        val search = menu?.findItem(R.id.menu_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("aaa", "search list submit")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                textSearch = newText
                adapter.filter.filter(newText)
                return false
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun eventBtnAdd() {
        btn_add.setOnClickListener {
            showDialog(CalendarDateModel(Calendar.getInstance().time, true))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDialog(date: CalendarDateModel) {
        val dialog = Dialog(this.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.input_note_dialog)

        val closeBtn = dialog.findViewById(R.id.btn_close_dialog) as ImageButton
        val saveBtn = dialog.findViewById(R.id.btn_save_dialog) as TextView
        val detailBtn = dialog.findViewById(R.id.btn_detail_dialog) as TextView
        val textDate = dialog.findViewById(R.id.textDate) as TextView
        val editEventBody = dialog.findViewById(R.id.editTextBody) as EditText
        val editEventName = dialog.findViewById(R.id.name_event_dialog) as EditText
        textDate.text = SimpleDateFormat("dd/MM/yyyy").format(date.data.time)

        saveBtn.setOnClickListener {
            val eName = editEventName.text.toString()
            val eBody = editEventBody.text.toString()
            if (eName != "" && eBody != "") {
                val id: Long = 0
                val positionAdd = listData.addItem(
                    DataNoteItem(
                        id,
                        date,
                        eName,
                        eBody
                    )
                ) // khong nen cho bien toan cuc vao day
                if (positionAdd != -1) {// neu add duoc
                    recycler_listNote.adapter?.notifyItemInserted(positionAdd)
                    recycler_listNote.layoutManager?.scrollToPosition(positionAdd)
                    if (textSearch != null) {
                        adapter.filter.filter(textSearch)
                    }

                    dialog.dismiss()
                    Toast.makeText(
                        this.context,
                        "Success id = $positionAdd",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
        }
        detailBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "go to detail", Toast.LENGTH_SHORT).show()
            val id = listData.getNewId()
            val eName = editEventName.text.toString()
            val eBody = editEventBody.text.toString()
            val item = DataNoteItem(id, date, eName, eBody)

            val bundle = Bundle()
            bundle.putSerializable(NOTE, item)
            val detailFragment = DetailFragment(listData)
            detailFragment.arguments = bundle

            activity?.supportFragmentManager?.commit {
                replace(R.id.fragment_view_main, detailFragment)
                setReorderingAllowed(true)
                addToBackStack(DETAIL_TAG)
            }
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDialogAction(item: DataNoteItem) {
        val dialog = Dialog(this.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.action_dialog)

        val closeBtn = dialog.findViewById(R.id.btn_close_dialog) as ImageButton
        val updateBtn = dialog.findViewById(R.id.btn_update_dialog) as TextView
        val detailBtn = dialog.findViewById(R.id.btn_detail_dialog) as TextView
        val deleteBtn = dialog.findViewById(R.id.btn_delete_dialog) as TextView
        val textDate = dialog.findViewById(R.id.textDate) as TextView
        val textTitle = dialog.findViewById(R.id.name_dialog) as TextView
        val editEventBody = dialog.findViewById(R.id.editTextBody) as EditText
        val editEventName = dialog.findViewById(R.id.name_event_dialog) as EditText

        textTitle.text = "id: ${item.noteId}"
        textDate.text = SimpleDateFormat("dd/MM/yyyy").format(item.date.data)
        editEventName.setText(item.titleHead)
        editEventBody.setText(item.titleBody)

        updateBtn.setOnClickListener {
            val eName = editEventName.text.toString()
            val eBody = editEventBody.text.toString()
            if (eName != "" && eBody != "") {
                val id: Long = 0
                val positionUpdate = listData.updateItem(
                    DataNoteItem(
                        id,
                        item.date,
                        eName,
                        eBody
                    )
                ) // khong nen cho bien toan cuc vao day
                if (positionUpdate != -1) {// neu add duoc
                    recycler_listNote.adapter?.notifyItemInserted(positionUpdate)
                    recycler_listNote.layoutManager?.scrollToPosition(positionUpdate)
                    if (textSearch != null) {
                        adapter.filter.filter(textSearch)
                    }

                    dialog.dismiss()
                    Toast.makeText(
                        this.context,
                        "Success id = $positionUpdate",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
        }
        detailBtn.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle()
            bundle.putSerializable(NOTE, item)
            val detailFragment = DetailFragment(listData)
            detailFragment.arguments = bundle

            activity?.supportFragmentManager?.commit {
                replace(R.id.fragment_view_main, detailFragment)
                setReorderingAllowed(true)
                addToBackStack(DETAIL_TAG)
            }
        }
        deleteBtn.setOnClickListener {
            dialog.dismiss()
            val itemRemoved = listData.deleteItem(item)
            if (textSearch != null)
                adapter.filter.filter(textSearch)
            else adapter.notifyDataSetChanged()
            Toast.makeText(this.requireContext(), "Delete ", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun eventSwipedRecyclerView() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                recycler_listNote.adapter?.notifyItemMoved(
                    viewHolder.layoutPosition,
                    target.layoutPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val positionOfData = viewHolder.layoutPosition
                when (direction) {
                    ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT -> {
                        Toast.makeText(
                            this@ToDoListFragment.requireContext(),
                            "Remove ${listData.getItem(positionOfData).titleHead}",
                            Toast.LENGTH_SHORT
                        ).show()
                        listData.deleteItem(listData.getItem(positionOfData))
                        recycler_listNote.adapter?.notifyItemRemoved(positionOfData)
                        if (textSearch != null)
                            adapter.filter.filter(textSearch)
                    }
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recycler_listNote)
    }

    private fun printList() {
        Log.d("aaa", listData.listToString())
    }

    private fun initRecyclerView() {
        recycler_listNote.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recycler_listNote.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onItemClick(v: View?, item: DataNoteItem) {
        showDialogAction(item)
    }
}