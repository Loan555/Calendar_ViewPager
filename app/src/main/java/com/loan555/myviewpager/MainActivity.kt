package com.loan555.myviewpager

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.loan555.myviewpager.adapter.ViewPagerAdapter
import com.loan555.myviewpager.adapter.startDayOfWeek
import com.loan555.myviewpager.model.CalendarDateModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

var lastClickPositionPager = ArrayList<Int>()
var lastSelectDateTime = CalendarDateModel(Calendar.getInstance().time)
var startPosition = 500
var currentPosition = startPosition
var lastDoubleTabPosition = CalendarDateModel(Calendar.getInstance().time)
var dayToChose = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayToChose)
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = mArrayAdapter
        spinner.gravity = 1
        spinner.onItemSelectedListener = this

        val adapter = ViewPagerAdapter()
        view_pager2.adapter = adapter
        view_pager2.setCurrentItem(startPosition, false)

        view_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("aabb", "onPageSelected + $position")
                currentPosition = position
                /** kiem tra xem trang nay co thuoc mot trong so nhung trang da click hay khong thi load lai */
//                for (i in 0 until lastClickPositionPager.size) {
//                    if (lastClickPositionPager[i] == position) {
//                        view_pager2.adapter?.notifyItemChanged(position, false)
//                        Log.d("aaa", "-------------------load lai trang nay")
//                        lastClickPositionPager.removeAt(i)
//                        break
//                    }
//                }
                view_pager2.adapter?.notifyItemChanged(position, false)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.d("aabb", "state scroll = $state")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        startDayOfWeek = position
        view_pager2.adapter?.notifyItemChanged(currentPosition)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        startDayOfWeek = 0
    }
}