package com.loan555.myviewpager.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loan555.myviewpager.*
import com.loan555.myviewpager.fragment.HomeFragment
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataDateTime
import kotlinx.android.synthetic.main.itempager.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var startDayOfWeek = 0

class ViewPagerAdapter(private var listener: OnInputNote) : RecyclerView.Adapter<ViewPagerVH>(), ItemAdapter.OnItemClickListener {
    //array of colors to change the background color of screen
    private val colors = intArrayOf(
        android.R.color.holo_blue_bright,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )
    lateinit var arrDate: ArrayList<CalendarDateModel> // luu ngay
    private var cal = Calendar.getInstance()
    private var arrDayOfWeek = ArrayList<String>() // luu thu
    private val numberOfColumns = 7

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerVH =
        ViewPagerVH(LayoutInflater.from(parent.context).inflate(R.layout.itempager, parent, false))

    override fun onBindViewHolder(holder: ViewPagerVH, position: Int) = holder.itemView.run {
        Log.d("aaa", "onBindViewHolder adapter ViewPager $position")
        when (position) {
            position -> {
                cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, position - startPosition)

                name_item.text = "ViewPager+ $position"
                mmmm_yyyy.text = SimpleDateFormat("MMMM yyyy").format(cal.time).toString()
                when(cal.time.month){
                    0,1,2 -> img.setImageResource(R.drawable.xuan)
                    3,4,5 -> img.setImageResource(R.drawable.ha)
                    6,7,8 -> img.setImageResource(R.drawable.thu)
                    else -> img.setImageResource(R.drawable.dong)
                }

                container.setBackgroundResource(colors[position % 4])

                /**Lay du lieu cho thang
                 *
                 */

                var mDataDateTime = DataDateTime()
                arrDate = mDataDateTime.getListForMonth(startDayOfWeek, cal)
                // hien thi thu
                setStartDayOdWeek() // khoi tao menu
                dayofweek.layoutManager = GridLayoutManager(this.context, numberOfColumns)
                dayofweek.adapter = MenuDayAdapter(arrDayOfWeek)

                recycler_view_date.layoutManager =
                    GridLayoutManager(this.context, numberOfColumns)
                recycler_view_date.adapter = ItemAdapter(arrDate, this@ViewPagerAdapter)
            }
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onItemClick(v: View?, position: Int, date: CalendarDateModel) {
        listener.onInputDialog(date)
    }

    private fun setStartDayOdWeek() {
        arrDayOfWeek.clear()
        for (i in 0..6) {
            arrDayOfWeek.add(SimpleDateFormat("EEE").format(arrDate[i].data.time).toString())
        }
    }

    interface OnInputNote{
        fun onInputDialog(date: CalendarDateModel)
    }

}

class ViewPagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
