package com.koaladev.recycly.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koaladev.recycly.data.response.RewardItem
import com.koaladev.recycly.databinding.ItemRewardBinding

class RewardAdapter(private val onRedeemClick: (RewardItem) -> Unit) :
    ListAdapter<RewardItem, RewardAdapter.RewardViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val binding = ItemRewardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RewardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = getItem(position)
        holder.bind(reward)
        holder.binding.btnRedeem.setOnClickListener {
            onRedeemClick(reward)
        }
    }

    class RewardViewHolder(val binding: ItemRewardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reward: RewardItem) {
            binding.tvRewardTitle.text = reward.title
            binding.tvRewardDescription.text = reward.description
            binding.btnRedeem.text = "Tukar ${reward.redeemPoint} Poin"
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RewardItem>() {
            override fun areItemsTheSame(oldItem: RewardItem, newItem: RewardItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RewardItem, newItem: RewardItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}