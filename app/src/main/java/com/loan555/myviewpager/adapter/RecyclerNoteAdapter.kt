package com.loan555.myviewpager.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan555.myviewpager.R
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import java.text.SimpleDateFormat

class RecyclerNoteAdapter(val listData: DataNoteList, val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerNoteAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textViewDate: TextView = itemView.findViewById(R.id.textView_date)
        var textViewHeader: TextView = itemView.findViewById(R.id.textView_titleHead)
        var textViewBody: TextView = itemView.findViewById(R.id.textView_titleBody)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("aaa", "nhan vao ${listData.getItem(position).titleHead}")
            listener.onItemClick(v,listData.getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textViewDate.text =
            SimpleDateFormat("d MMMM yyyy").format(listData.getItem(position).date.data.time)
        holder.textViewHeader.text = listData.getItem(position).titleHead
        holder.textViewBody.text = listData.getItem(position).titleBody
    }

    override fun getItemCount(): Int {
        return listData.getSize()
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, item : DataNoteItem)
    }
}
