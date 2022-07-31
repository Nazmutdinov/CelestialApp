package com.example.celestialapp.presentation.fragments

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(
    private val callbackSwipeLeft: (Int) -> Unit
): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition

        if (direction == ItemTouchHelper.LEFT) callbackSwipeLeft(position)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }


}