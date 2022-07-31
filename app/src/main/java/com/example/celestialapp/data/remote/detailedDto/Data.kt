package com.example.celestialapp.data.remote.detailedDto

data class Data(
    val center: String,
    val date_created: String,
    val description: String,
    val keywords: List<String>,
    val nasa_id: String,
    val title: String
)