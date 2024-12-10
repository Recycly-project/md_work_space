package com.koaladev.recycly.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koaladev.recycly.data.response.WasteCollectionsItem
import com.koaladev.recycly.databinding.ItemHistoryListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<WasteCollectionsItem, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(private val binding: ItemHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WasteCollectionsItem) {
            binding.apply {
                tvTitle.text = item.label
                tvPoints.text = "Points: ${item.points}"
                tvSubmittedDate.text = "Submitted on: ${formatDate(item.createdAt)}"
            }
        }
        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date!!)
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WasteCollectionsItem>() {
            override fun areItemsTheSame(oldItem: WasteCollectionsItem, newItem: WasteCollectionsItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: WasteCollectionsItem, newItem: WasteCollectionsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}