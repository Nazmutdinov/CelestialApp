package com.example.celestialapp.domain.models

data class CelestialDataItem(
    val nasaId: String,
    // uri путь к фото
    val imagePath: String,
    // признак, что небесное тело находится в списке избранных в локальной БД
    var favourite: Boolean
)
