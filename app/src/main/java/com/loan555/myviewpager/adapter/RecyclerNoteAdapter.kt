package com.loan555.myviewpager.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan555.myviewpager.R
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import java.text.SimpleDateFormat
import kotlin.coroutines.coroutineContext

class RecyclerNoteAdapter(var listDataOld: DataNoteList, val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerNoteAdapter.MyViewHolder>(), Filterable {
    var listData = listDataOld.getList()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textViewDate: TextView = itemView.findViewById(R.id.textView_date)
        var textViewHeader: TextView = itemView.findViewById(R.id.textView_titleHead)
        var textViewBody: TextView = itemView.findViewById(R.id.textView_titleBody)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("aaa", "nhan vao ${listData[position].titleHead}")
            listener.onItemClick(v, listData[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textViewDate.text =
            SimpleDateFormat("d MMMM yyyy").format(listData[position].date.data.time)
        holder.textViewHeader.text = listData[position].titleHead
        holder.textViewBody.text = listData[position].titleBody
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, item: DataNoteItem)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listData = listDataOld.getList()
                } else {
                    val resultList = ArrayList<DataNoteItem>()
                    listDataOld.getList().forEach {
                        if (it.titleHead.toLowerCase()
                                .contains(charSearch) || it.titleBody.toLowerCase()
                                .contains(charSearch)
                        ) {
                            resultList.add(it)
                        }
                    }
                    listData = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listData
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listData = results?.values as ArrayList<DataNoteItem>
                notifyDataSetChanged()
            }

        }
    }
}
