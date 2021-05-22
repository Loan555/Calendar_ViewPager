package com.loan555.myviewpager.adapter

import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan555.myviewpager.R
import com.loan555.myviewpager.lastDoubleTabPosition
import com.loan555.myviewpager.lastSelectDateTime
import com.loan555.myviewpager.model.CalendarDateModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

var oldClick = -1
var boubleTabColor = Color.parseColor("#FFBB86FC")
var checkClick = false

class ItemAdapter(
    private var listData: ArrayList<CalendarDateModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ItemAdapter.ViewHolder3>() {
    var i: Int = 0
    val handler = Handler()
    val r = Runnable {
        i = 0
        Log.d("rrrr", "runable ")
    }

    inner class ViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.item_text)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = layoutPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(v, adapterPosition)

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
                Log.d("aaa", "--------------------notify xong nos di dau old click = $oldClick")
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
        Log.d("aaa", "bind du lieu tai : $position")
        //ngay thang khac
        if (listData[position].data.month != listData[9].data.month) {
            holder.textView.setTextColor(Color.parseColor("#5E888888"))
            Log.d(
                "colorrr",
                "mau thangs khac position = $position ,${listData[position].data.date}/ ${listData[position].data.month + 1}"
            )
        }
        when (true) {
            //double tab
            (!checkClick && oldClick == position && lastDoubleTabPosition.data.date == listData[position].data.date && lastDoubleTabPosition.data.month == listData[position].data.month && lastDoubleTabPosition.data.year == listData[position].data.year) -> {
                holder.textView.setBackgroundColor(boubleTabColor)
                return
            }
            // click
            (checkClick && oldClick == position && lastSelectDateTime.data.date == listData[position].data.date && lastSelectDateTime.data.month == listData[position].data.month && lastSelectDateTime.data.year == listData[position].data.year) -> {
                holder.textView.setBackgroundColor(Color.parseColor("#FF6200EE"))
                Log.d(
                    "colorrr",
                    "---------------------mau click position = $position ,${listData[position].data.date}/ ${listData[position].data.month + 1}"
                )
                return
            }
            //today
            (Calendar.getInstance().time.date == listData[position].data.date
                    && Calendar.getInstance().time.month == listData[position].data.month
                    && Calendar.getInstance().time.year == listData[position].data.year)
            -> {
                holder.textView.setBackgroundColor(Color.parseColor("#FFBB86FC"))
                return
            }
            // ngay trong thang
            else -> {
                holder.textView.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    //-----------------

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }
}


