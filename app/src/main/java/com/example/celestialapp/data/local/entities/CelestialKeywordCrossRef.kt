package com.example.celestialapp.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["celestialId", "keywordId"])
data class CelestialKeywordCrossRef(
    val celestialId: Int,
    val keywordId: Int
)
