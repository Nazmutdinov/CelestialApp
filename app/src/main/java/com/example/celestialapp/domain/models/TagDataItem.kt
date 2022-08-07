package com.example.celestialapp.domain.models

import com.example.celestialapp.R


data class TagDataItem(
    val tagId: Int,
    val name: String,
    var selected: Boolean = false,
    var color: Int = R.color.white
) {
    fun toggle() {
        selected = !selected
        color = if (selected) R.color.purple_200 else R.color.white
    }
}
