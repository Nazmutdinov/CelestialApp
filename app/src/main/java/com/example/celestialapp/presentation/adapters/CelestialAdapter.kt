package com.example.celestialapp.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Dimension
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.example.celestialapp.R
import com.example.celestialapp.databinding.CelestialItemBinding
import com.example.celestialapp.domain.models.CelestialDataItem

// адаптер небесного тела
class CelestialAdapter(
     private val callbackItemClick: (CelestialDataItem) -> Unit
) : PagingDataAdapter<CelestialDataItem, CelestialAdapter.Holder>(DiffCallback) {
    class Holder(val binding: CelestialItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            CelestialItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position) ?: return

       with(holder.binding) {
            imageView.load(item.imagePath) {
                placeholder(R.drawable.planet)
                transformations(RoundedCornersTransformation())
            }

            // слушаем тап по картинке небесного тела
            imageView.setOnClickListener {
                callbackItemClick(item)
            }
       }
    }

    object DiffCallback : DiffUtil.ItemCallback<CelestialDataItem>() {
        override fun areContentsTheSame(
            oldItem: CelestialDataItem,
            newItem: CelestialDataItem
        ) = oldItem.nasaId == newItem.nasaId


        override fun areItemsTheSame(
            oldItem: CelestialDataItem,
            newItem: CelestialDataItem
        ) = oldItem.nasaId == newItem.nasaId

    }

}