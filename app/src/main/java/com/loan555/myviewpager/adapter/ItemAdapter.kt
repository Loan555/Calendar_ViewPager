package com.loan555.myviewpager.adapter

import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan555.myviewpager.*
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

var oldClick = -1
var boubleTabColor = Color.parseColor("#AA66CC")
var checkClick = false

class ItemAdapter(
    private var listData: ArrayList<CalendarDateModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ItemAdapter.ViewHolder3>() {
    var i: Int = 0
    val handler = Handler()
    val r = Runnable {
        i = 0
    }
    //array of colors to change the background color of screen
    private val colors = intArrayOf(
        android.R.color.holo_blue_bright,
        android.R.color.white,//day
        android.R.color.holo_blue_dark, //color click
        android.R.color.holo_purple,     //color today
        android.R.color.holo_orange_dark     //color day have event
    )

    inner class ViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.item_text)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = layoutPosition
            if (listData[position].data.month == listData[9].data.month && position != RecyclerView.NO_POSITION) {
                i++
                if (i == 1) {
                    //Single click
                    handler.postDelayed(r, 250);
                    Log.d("aaa", "click = $position")
                    lastSelectDateTime = listData[position]
                    checkClick = true
                } else if (i == 2) {
                    //Double click
                    i = 0;
                    if (oldClick == position) {
                        Log.d("aaa", "double click = $position")
                        checkClick = false
                        lastDoubleTabPosition = listData[position]
                        listener.onItemClick(v, adapterPosition, listData[position])
                        boubleTabColor = Color.argb(
                            255,
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                    } else {
                        Log.d("aaa", "click = $position")
                        lastSelectDateTime = listData[position]
                    }
                }
                if (oldClick != -1)
                    notifyItemChanged(oldClick, false)
                notifyItemChanged(position, false)
                oldClick = position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder3 {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_item_adapter, parent, false)
        return ViewHolder3(view)
    }

    override fun onBindViewHolder(holder: ViewHolder3, position: Int) {
        holder.textView.text = listData[position].data.date.toString()
        //ngay thang khac
        if (listData[position].data.month != listData[9].data.month) {
            holder.textView.setTextColor(Color.parseColor("#5E888888"))
        }
        when (true) {
            //double tab
            (!checkClick && listData[position].data.month == listData[9].data.month && lastDoubleTabPosition.data.date == listData[position].data.date && lastDoubleTabPosition.data.month == listData[position].data.month && lastDoubleTabPosition.data.year == listData[position].data.year) -> {
                holder.textView.setBackgroundColor(boubleTabColor)
                return
            }
            // click
            (checkClick && listData[position].data.month == listData[9].data.month && lastSelectDateTime.data.date == listData[position].data.date && lastSelectDateTime.data.month == listData[position].data.month && lastSelectDateTime.data.year == listData[position].data.year) -> {
                holder.textView.setBackgroundColor(Color.parseColor("#0099CC"))
                return
            }
            //today
            (listData[position].data.month == listData[9].data.month && Calendar.getInstance().time.date == listData[position].data.date
                    && Calendar.getInstance().time.month == listData[position].data.month
                    && Calendar.getInstance().time.year == listData[position].data.year)
            -> {
                holder.textView.setBackgroundColor(Color.parseColor("#AA66CC"))
                return
            }
            //ngay co su kien
            listData[position].haveEvent && listData[position].data.month == listData[9].data.month->{
                holder.textView.setBackgroundColor(Color.parseColor("#FF8800"))
                return
            }
            // ngay trong thang
            else -> {
                holder.textView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    //-----------------

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int, date: CalendarDateModel)
    }
}


