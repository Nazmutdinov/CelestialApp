package com.example.celestialapp.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialapp.R
import com.example.celestialapp.databinding.KeywordItemBinding
import com.example.celestialapp.domain.models.TagDataItem

class KeywordAdapter(
    private val context: Context,
    private val callbackItemClick: (TagDataItem) -> Unit) :
    ListAdapter<TagDataItem, KeywordAdapter.Holder>(ItemDiffCallback) {

    class Holder(val binding: KeywordItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = KeywordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            if (item.selected) {
                cardView.setCardBackgroundColor(context.getColor(R.color.purple_200))
                nameTextView.background = context.resources.getDrawable(R.color.purple_200, context.theme)
            } else {
                cardView.setCardBackgroundColor(context.getColor(R.color.white))
                nameTextView.background = context.resources.getDrawable(R.color.white, context.theme)
            }

            nameTextView.text = item.name

            nameTextView.setOnClickListener {
                callbackItemClick(item)
            }
        }
    }

    object ItemDiffCallback : DiffUtil.ItemCallback<TagDataItem>() {
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