package com.loan555.myviewpager.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.loan555.myviewpager.*
import com.loan555.myviewpager.adapter.ViewPagerAdapter
import com.loan555.myviewpager.adapter.startDayOfWeek
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import java.text.SimpleDateFormat

const val NOTE = "NOTE_KEY"
const val NOTE_DATE = "NOTE_KEY_DATE"

class HomeFragment(var dataNoteList: DataNoteList) : Fragment(), AdapterView.OnItemSelectedListener,
    ViewPagerAdapter.OnInputNote {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("aaa", "onCreateView")

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("aaa", "onViewCreated")

        initSpinner()
        initViewPager()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        startDayOfWeek = position
        view_pager2.adapter?.notifyItemChanged(currentPosition, false)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        startDayOfWeek = 0
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onInputDialog(date: CalendarDateModel) {
        showDialog(date)
    }

    private fun initSpinner() {
        var mArrayAdapter = activity?.let {
            ArrayAdapter(
                it.applicationContext,
                android.R.layout.simple_spinner_item,
                dayToChose
            )
        }
        mArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = mArrayAdapter
        spinner.gravity = 1
        spinner.onItemSelectedListener = this
    }

    private fun initViewPager() {
        val adapter = ViewPagerAdapter(this, dataNoteList)
        view_pager2.adapter = adapter
        view_pager2.setCurrentItem(startPosition, false)

        view_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                view_pager2.adapter?.notifyItemChanged(currentPosition, false)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDialog(date: CalendarDateModel) {
        val dialog = Dialog(this.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_home)

        val closeBtn = dialog.findViewById(R.id.btn_close_dialog) as ImageButton
        val saveBtn = dialog.findViewById(R.id.btn_save_dialog) as TextView
        val detailBtn = dialog.findViewById(R.id.btn_detail_dialog) as TextView
        val toListBtn = dialog.findViewById(R.id.btn_list_dialog) as TextView
        val textDate = dialog.findViewById(R.id.textDate) as TextView
        val editEventBody = dialog.findViewById(R.id.editTextBody) as EditText
        val editEventName = dialog.findViewById(R.id.name_event_dialog) as EditText

        textDate.text = SimpleDateFormat("dd/MM/yyyy").format(date.data.time)

        saveBtn.setOnClickListener {
            val eName = editEventName.text.toString()
            val eBody = editEventBody.text.toString()
            if (eName != "" && eBody != "") {
                val id = dataNoteList.getNewId()
                val positionAdd = dataNoteList.addItem(
                    DataNoteItem(
                        id,
                        date,
                        eName,
                        eBody
                    )
                ) // khong nen cho bien toan cuc vao day
                dialog.dismiss()
                Toast.makeText(
                    this.context,
                    "Add : $positionAdd",
                    Toast.LENGTH_SHORT
                ).show()
                view_pager2.adapter?.notifyDataSetChanged()
            }
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
        }
        detailBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this.requireContext(), "go to detail", Toast.LENGTH_SHORT).show()
            val id = dataNoteList.getNewId()
            val eName = editEventName.text.toString()
            val eBody = editEventBody.text.toString()
            val item = DataNoteItem(id, date, eName, eBody)

            val bundle = Bundle()
            bundle.putSerializable(NOTE, item)
            val detailFragment = DetailFragment(dataNoteList)
            detailFragment.arguments = bundle

            activity?.supportFragmentManager?.commit {
                replace(R.id.fragment_view_main, detailFragment)
                setReorderingAllowed(true)
                addToBackStack(DETAIL_TAG)
            }
        }
        toListBtn.setOnClickListener {
            dialog.dismiss()
            activity?.supportFragmentManager?.commit {
                val mToDoListFragment = ToDoListFragment(dataNoteList)
                replace(R.id.fragment_view_main, mToDoListFragment)
                setReorderingAllowed(true)
                addToBackStack(LIST_TAG)
            }
        }
        dialog.show()
    }

}