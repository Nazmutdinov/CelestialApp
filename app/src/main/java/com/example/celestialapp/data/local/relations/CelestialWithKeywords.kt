package com.example.celestialapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.celestialapp.data.local.entities.CelestialInfoEntity
import com.example.celestialapp.data.local.entities.KeywordInfoEntity

data class CelestialWithKeywords(
    @Embedded val celestialInfoEntity: CelestialInfoEntity,
    @Relation(
        parentColumn = "celestialId",
        entityColumn = "keywordId"
    )
    val keywordInfoEntity: List< KeywordInfoEntity>
)
