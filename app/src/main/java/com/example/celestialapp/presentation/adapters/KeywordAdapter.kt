package com.example.celestialapp.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

        onBind(holder, item)
    }

    private fun onBind(holder: Holder, item: TagDataItem) {
        with(holder.binding) {
            cardView.setCardBackgroundColor(context.getColor(item.color))

            nameTextView.apply {
                background = ResourcesCompat.getDrawable(context.resources, item.color, context.theme)
                text = item.name

                setOnClickListener {
                    callbackItemClick(item)
                }
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