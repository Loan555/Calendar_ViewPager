package com.loan555.myviewpager.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.loan555.myviewpager.*
import com.loan555.myviewpager.adapter.ViewPagerAdapter
import com.loan555.myviewpager.adapter.startDayOfWeek
import com.loan555.myviewpager.model.AppPreferences
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

const val NOTE = "NOTE_KEY"

class HomeFragment(var dataNoteList: DataNoteList) : Fragment(), AdapterView.OnItemSelectedListener,
    ViewPagerAdapter.OnInputNote {
    var dayToChose = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        initViewPager()
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
                view_pager2.adapter?.notifyItemChanged(position, false)
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
        //get data
        val id: Long = -1
        val eName = editEventName.text.toString()
        val eBody = editEventBody.text.toString()
        val item = DataNoteItem(id, date, eName, eBody)
        val bundle = Bundle()
        bundle.putSerializable(NOTE, item)

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
                )
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

            //set arguments for DetailFragment
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
                mToDoListFragment.arguments = bundle
                replace(R.id.fragment_view_main, mToDoListFragment)
                setReorderingAllowed(true)
                addToBackStack(LIST_TAG)
            }
        }
        dialog.show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        startDayOfWeek = position
        view_pager2.adapter?.notifyItemChanged(view_pager2.currentItem, false)
        AppPreferences.init(this.requireContext())
        AppPreferences.startDayOfWeek = startDayOfWeek
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        AppPreferences.init(this.requireContext())
        startDayOfWeek = AppPreferences.startDayOfWeek
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onInputDialog(date: CalendarDateModel) {
        showDialog(date)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_date -> {
                datePicker()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun datePicker() {
        val cal: Calendar = Calendar.getInstance()
        val y: Int = cal.get(Calendar.YEAR)
        val m: Int = cal.get(Calendar.MONTH)
        val d: Int = cal.get(Calendar.DAY_OF_MONTH)

        var picker = Dialog(this.requireContext())
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE)
        picker.setContentView(R.layout.picker_date_dialog)

        val datePicker = picker.findViewById<DatePicker>(R.id.date_picker)
        val btnSetDate = picker.findViewById<TextView>(R.id.btn_set_date)
        val btnCane = picker.findViewById<TextView>(R.id.btn_cane_dialog)
        val mDate = Date(y,m,d)

        datePicker.init(
            y,
            m,
            d,
            DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                mDate.year = year
                mDate.month = monthOfYear
                mDate.date = dayOfMonth
            })

        btnSetDate.setOnClickListener {
            picker.dismiss()
            lastSelectDateTime = CalendarDateModel(mDate, false)
            view_pager2.currentItem =
                startPosition - (y - mDate.year) * 12 - (m - mDate.month)
            Toast.makeText(this.context, "go to date ${view_pager2.currentItem} : $mDate", Toast.LENGTH_SHORT).show()
        }
        btnCane.setOnClickListener {
            picker.dismiss()
        }
        picker.show()
    }
}