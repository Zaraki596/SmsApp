package com.example.smsapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smsapp.R
import com.example.smsapp.data.model.ViewItem
import com.example.smsapp.utils.checkTimeRange

class SmsListAdapter() :
    ListAdapter<ViewItem, SmsListAdapter.SmsListViewHolder>(SmsItemDC()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SmsListViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
    )

    override fun onBindViewHolder(holder: SmsListViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<ViewItem>) {
        submitList(data)
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].resource
    }

    inner class SmsListViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ViewItem) = with(itemView) {
            when (item) {
                is ViewItem.SMSItem -> {
                    this.findViewById<TextView>(R.id.tvBody).text = item.smsItem.body
                    this.findViewById<TextView>(R.id.tvNumber).text = item.smsItem.number
                    this.findViewById<TextView>(R.id.tvDate).text = item.smsItem.formattedDate
                }
                is ViewItem.DateItem -> {
                    this.findViewById<TextView>(R.id.tvHoursPassed).text =
                        checkTimeRange(item.hoursItem.hoursPassed)

                }
            }
        }
    }

    private class SmsItemDC : DiffUtil.ItemCallback<ViewItem>() {
        override fun areItemsTheSame(
            oldItem: ViewItem,
            newItem: ViewItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ViewItem,
            newItem: ViewItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}