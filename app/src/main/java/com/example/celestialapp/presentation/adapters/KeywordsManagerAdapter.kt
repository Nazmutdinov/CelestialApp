package com.example.celestialapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialapp.databinding.ManagerKeywordItemBinding
import com.example.celestialapp.domain.models.TagDataItem

class KeywordsManagerAdapter :
    ListAdapter<TagDataItem, KeywordsManagerAdapter.Holder>(ItemDiffCallback())
     {

    class Holder(val binding: ManagerKeywordItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ManagerKeywordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.binding.nameTextView.text = item.name
    }


    class ItemDiffCallback : DiffUtil.ItemCallback<TagDataItem>() {
        override fun areItemsTheSame(
            oldItem: TagDataItem,
            newItem: TagDataItem
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: TagDataItem,
            newItem: TagDataItem
        ): Boolean = oldItem == newItem
    }
}

