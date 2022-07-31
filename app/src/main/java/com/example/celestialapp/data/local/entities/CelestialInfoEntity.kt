package com.example.celestialapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CelestialInfoEntity(
    @PrimaryKey(autoGenerate = true) val celestialId: Int = 0,
    @ColumnInfo(name = "nasa_id") val nasaId: String,
    val title: String,
    val date: String,
    val dateFavouriteCreated: Long? = null,
    val description: String,
    var image: ByteArray? = null,
    @ColumnInfo(name = "image_path") val imagePath: String
)
