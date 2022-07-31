package com.example.celestialapp.domain.models


data class TagDataItem(
    val tagId: Int,
    val name: String,
    var selected: Boolean
) {
    /**
     * переключить selected состояние тега
     */
    fun toggle() {
        selected = !selected
    }
}
