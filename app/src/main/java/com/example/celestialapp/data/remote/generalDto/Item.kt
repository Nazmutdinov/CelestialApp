package com.example.celestialapp.data.remote.generalDto

data class Item(
    val `data`: List<Data>,
    val href: String,
    val links: List<Link>
)