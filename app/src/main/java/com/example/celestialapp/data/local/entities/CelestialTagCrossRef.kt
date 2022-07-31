package com.example.celestialapp.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["celestialId","tagId"])
data class CelestialTagCrossRef(
    val celestialId: Int,
    val tagId: Int
)
