package com.example.celestialapp.data.repository

import com.example.celestialapp.R
import com.example.celestialapp.data.local.entities.CelestialInfoEntity
import com.example.celestialapp.data.local.entities.TagInfoEntity
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem

class LocalDataMapper(
    private val utils: Utils
) {
    fun getSelectedTagDataItem(
        tagInfoEntity: TagInfoEntity
    ): TagDataItem =
        TagDataItem(
            tagId = tagInfoEntity.tagId,
            name = tagInfoEntity.name,
            selected = true,
            color = R.color.purple_200
        )

    fun getUnselectedTagDataItem(
        tagInfoEntity: TagInfoEntity
    ): TagDataItem =
        TagDataItem(
            tagId = tagInfoEntity.tagId,
            name = tagInfoEntity.name,
            selected = false,
            color = R.color.white
        )

    fun mapCelestialInfoEntityToModel(
        celestialInfoEntity: CelestialInfoEntity
    ): FavouriteCelestialDataItem =
        FavouriteCelestialDataItem(
            celestialId = celestialInfoEntity.celestialId,
            nasaId = celestialInfoEntity.nasaId,
            title = celestialInfoEntity.title,
            photoOwnerCreatedYearsAgo = utils.getYearsAgo(celestialInfoEntity.date),
            description = celestialInfoEntity.description,
            image = celestialInfoEntity.image,
            imagePath = celestialInfoEntity.imagePath,
            dateWhenInDBSaved = celestialInfoEntity.dateFavouriteCreated
        )

    fun mapListCelestialInfoEntityToModel(
        listCelestialInfoEntity: List<CelestialInfoEntity>
    ): List<FavouriteCelestialDataItem> =
        listCelestialInfoEntity.map {
            mapCelestialInfoEntityToModel(it)
        }
}