package com.example.celestialapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.celestialapp.data.local.entities.CelestialInfoEntity
import com.example.celestialapp.data.local.entities.CelestialTagCrossRef
import com.example.celestialapp.data.local.entities.TagInfoEntity

data class CelestialWithTags(
    @Embedded val celestialInfoEntity: CelestialInfoEntity,
    @Relation(
        parentColumn = "celestialId",
        entityColumn = "tagId",
        associateBy = Junction(CelestialTagCrossRef::class)
    )
    val tagInfoEntity: List<TagInfoEntity>
)

