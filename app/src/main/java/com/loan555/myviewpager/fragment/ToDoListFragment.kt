package com.loan555.myviewpager.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import kotlinx.android.synthetic.main.itempager.*
import java.text.SimpleDateFormat
import java.util.*

var mDataNoteList = DataNoteList("resource")

class ToDoListFragment : Fragment(), RecyclerNoteAdapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
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
        eventSwipedRecyclerView()
        eventBtnAdd()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun eventBtnAdd() {
        btn_add.setOnClickListener {
            showDialog(CalendarDateModel(Calendar.getInstance().time))
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

                Log.d(
                    "aaa",
                    "ban vua them $eName - $eBody - ${SimpleDateFormat("d MMMM yyyy").format(date.data.time)}"
                )
                val id = mDataNoteList.getNewId()
                mDataNoteList.addItem(
                    DataNoteItem(
                        id,
                        date,
                        eName,
                        eBody
                    )
                ) // khong nen cho bien toan cuc vao day
                mDataNoteList.sortByDateDescending()

                dialog.dismiss()

                Toast.makeText(
                    this.context,
                    "Success id = $id",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
        }
        detailBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "go to detail", Toast.LENGTH_SHORT).show()
            val id = mDataNoteList.getNewId()
            val eName = editEventName.text.toString()
            val eBody = editEventBody.text.toString()
            val item = DataNoteItem(id, date, eName, eBody)

            val bundle = Bundle()
            bundle.putSerializable(NOTE, item)
            val detailFragment = DetailFragment()
            detailFragment.arguments = bundle

            activity?.supportFragmentManager?.commit {
                replace(R.id.fragment_view_main, detailFragment)
                setReorderingAllowed(true)
                addToBackStack(DETAIL_TAG)
            }
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
                            "Remove ${mDataNoteList.getItem(positionOfData).titleHead}",
                            Toast.LENGTH_SHORT
                        ).show()
                        mDataNoteList.deleteItem(mDataNoteList.getItem(positionOfData).noteId)
                        recycler_listNote.adapter?.notifyItemRemoved(positionOfData)
                    }
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recycler_listNote)
    }

    private fun printList() {
        Log.d("aaa", mDataNoteList.listToString())
    }

    private fun initRecyclerView() {
        recycler_listNote.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recycler_listNote.adapter = RecyclerNoteAdapter(mDataNoteList, this)
    }

    override fun onItemClick(v: View?, item: DataNoteItem) {
        v?.setBackgroundColor(Color.parseColor("#FF03DAC5"))

        val item = DataNoteItem(item.noteId, item.date, item.titleHead, item.titleBody)

        val bundle = Bundle()
        bundle.putSerializable(NOTE, item)
        val detailFragment = DetailFragment()
        detailFragment.arguments = bundle

        activity?.supportFragmentManager?.commit {
            replace(R.id.fragment_view_main, detailFragment)
            setReorderingAllowed(true)
            addToBackStack(DETAIL_TAG)
        }
    }
}