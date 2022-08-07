package com.example.celestialapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.celestialapp.R
import com.example.celestialapp.databinding.CelestialItemBinding
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem

class FavouriteCelestialAdapter(
    private val callbackItemClick: (FavouriteCelestialDataItem) -> Unit
) : ListAdapter<FavouriteCelestialDataItem, FavouriteCelestialAdapter.Holder>(DiffCallback) {
    class Holder(val binding: CelestialItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            CelestialItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position) ?: return

       onBind(holder, item)
    }

    private fun onBind(holder: Holder, item: FavouriteCelestialDataItem) {
        with(holder.binding.imageView) {
            load(item.image) {
                placeholder(R.drawable.planet)
                transformations(RoundedCornersTransformation())
            }

            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP

            setOnClickListener {
                callbackItemClick(item)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<FavouriteCelestialDataItem>() {
        override fun areContentsTheSame(
            oldItem: FavouriteCelestialDataItem,
            newItem: FavouriteCelestialDataItem
        ) = oldItem.celestialId == newItem.celestialId


        override fun areItemsTheSame(
            oldItem: FavouriteCelestialDataItem,
            newItem: FavouriteCelestialDataItem
        ) = oldItem.celestialId == newItem.celestialId
    }
}