// app/src/main/java/com/koaladev/recycly/adapter/QrHistoryAdapter.kt

package com.koaladev.recycly.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koaladev.recycly.data.response.QrHistoryItem
import com.koaladev.recycly.databinding.ItemHistoryListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class QrHistoryAdapter : ListAdapter<QrHistoryItem, QrHistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QrHistoryItem) {
            binding.tvTitle.text = "${item.bottles} Botol Terkumpul"
            binding.tvPoints.text = "+${item.points}" // Tampilkan poin yang didapat
            binding.tvSubmittedDate.text = "Dipindai pada: ${formatDate(item.scannedAt)}"
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QrHistoryItem>() {
            override fun areItemsTheSame(oldItem: QrHistoryItem, newItem: QrHistoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: QrHistoryItem, newItem: QrHistoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}