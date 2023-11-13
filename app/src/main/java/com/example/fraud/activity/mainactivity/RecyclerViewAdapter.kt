package com.example.fraud.activity.mainactivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fraud.R
import com.example.fraud.model.Transaction

class RecyclerViewAdapter(private val context: Context?, private val items: List<Transaction>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionTitle: TextView = itemView.findViewById(R.id.transactionTitle)
        val transactionDate: TextView = itemView.findViewById(R.id.transactionDate)
        val transactionAmount: TextView = itemView.findViewById(R.id.transactionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.transactionTitle.text = "Paid to " + item.merchant
        holder.transactionDate.text = item.transTime.removeRange(19,29)
        holder.transactionAmount.text = "$" + item.amt
        val bundle = Bundle()
        bundle.putSerializable("transaction", item)
        holder.itemView.setOnClickListener {
            holder.itemView.findNavController().navigate(R.id.transactionInfo,bundle)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
