package se.westpay.lamusica.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.R

class LogScanAdapter(private val logEntries: MutableList<String> = mutableListOf()) : RecyclerView.Adapter<LogScanAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logView: TextView = view.findViewById(R.id.searchItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogScanAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogScanAdapter.ViewHolder, position: Int) {
        holder.logView.text = logEntries[position]
    }

    override fun getItemCount(): Int {
        return logEntries.size
    }

    // Function to add an item dynamically
    fun addLogEntry(log: String) {
        logEntries.add(log)
        notifyItemInserted(logEntries.size - 1) // Notify RecyclerView about new item
    }

    // Function to remove an item dynamically
    fun removeLogEntry(position: Int) {
        if (position < logEntries.size) {
            logEntries.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}