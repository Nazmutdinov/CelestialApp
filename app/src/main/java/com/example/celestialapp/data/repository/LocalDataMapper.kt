package com.example.celestialapp.data.repository

import com.example.celestialapp.data.local.entities.CelestialInfoEntity
import com.example.celestialapp.data.local.entities.TagInfoEntity
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem


class LocalDataMapper(
    private val utils: Utils
) {
    /**
     * маппинг тега KeywordInfoEntity из БД в модель KeywordDataItem
     */
    fun mapTagInfoEntityToModel(
        tagInfoEntity: TagInfoEntity
    ): TagDataItem =
        TagDataItem(
            tagId = tagInfoEntity.tagId,
            name = tagInfoEntity.name,
            selected = true
        )

    /**
     * маппинг небесного тела CelestialInfoEntity из БД в модель FavouriteCelestialDataItem
     */
    fun mapCelestialInfoEntityToModel(
        celestialInfoEntity: CelestialInfoEntity
    ): FavouriteCelestialDataItem =
        FavouriteCelestialDataItem(
            celestialId = celestialInfoEntity.celestialId,
            nasaId = celestialInfoEntity.nasaId,
            title = celestialInfoEntity.title,
            yearsAgo = utils.getYearAgo(celestialInfoEntity.date),
            description = celestialInfoEntity.description,
            image =  celestialInfoEntity.image,
            imagePath = celestialInfoEntity.imagePath,
            orderDateCreated = celestialInfoEntity.dateFavouriteCreated
        )

    /**
     * маппинг списка небесных тел CelestialInfoEntity из БД в список моделей FavouriteCelestialDataItem
     */
    fun mapListCelestialInfoEntityToModel(
        listCelestialInfoEntity: List<CelestialInfoEntity>
    ): List<FavouriteCelestialDataItem> =
        listCelestialInfoEntity.map {
            mapCelestialInfoEntityToModel(it)
        }

}