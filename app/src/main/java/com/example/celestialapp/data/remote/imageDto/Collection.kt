package com.example.celestialapp.data.remote.imageDto

data class Collection(
    val href: String,
    val items: List<Item>,
    val version: String
)