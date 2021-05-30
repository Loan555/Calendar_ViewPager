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
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat

const val NOTE = "NOTE_KEY"

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener, ViewPagerAdapter.OnInputNote {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        val adapter = ViewPagerAdapter(this)
        view_pager2.adapter = adapter
        view_pager2.setCurrentItem(startPosition, false)

        view_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("aabb", "onPageSelected + $position")
                currentPosition = position
                view_pager2.adapter?.notifyItemChanged(currentPosition, false)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.d("aabb", "state scroll = $state")
            }
        })
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onInputDialog(date: CalendarDateModel) {
        showDialog(date)
    }

}