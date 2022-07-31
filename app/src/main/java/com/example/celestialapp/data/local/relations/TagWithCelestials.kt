package com.example.celestialapp.data.local.relations

import androidx.room.Embedded

import androidx.room.Junction
import androidx.room.Relation
import com.example.celestialapp.data.local.entities.CelestialInfoEntity
import com.example.celestialapp.data.local.entities.CelestialTagCrossRef
import com.example.celestialapp.data.local.entities.TagInfoEntity

data class TagWithCelestials(
    @Embedded val tagInfoEntity: TagInfoEntity,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "celestialId",
        associateBy = Junction(CelestialTagCrossRef::class)
    )
    val celestialInfoEntity: List<CelestialInfoEntity>
)
