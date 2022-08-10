package com.example.celestialapp.domain.models

data class FavouriteCelestialDataItem(
    // own local DB id
    val celestialId: Int,
    // built-in NASA id
    val nasaId: String,
    val title: String,
    // creater-owner - NASA means
    val photoOwnerCreatedYearsAgo: String,
    val dateWhenInDBSaved: Long? = null,
    val description: String,
    val image: ByteArray? = null,
    val imagePath: String
)
