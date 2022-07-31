package com.example.celestialapp.data.repository

import com.example.celestialapp.data.remote.generalDto.Item
import com.example.celestialapp.domain.models.CelestialDataItem

class RemoteDataMapper {
    fun mapDtoToModel(
        item: Item, favourite: Boolean
    ): CelestialDataItem {
        val nasaId = item.data.first().nasa_id ?: ""
        val imagePath =  item.links.first().href ?: ""


        return CelestialDataItem(nasaId, imagePath, favourite)
    }

}