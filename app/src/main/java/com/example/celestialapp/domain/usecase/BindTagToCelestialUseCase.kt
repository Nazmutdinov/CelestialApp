package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository
import java.util.*

class BindTagToCelestialUseCase(
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(tagId: Int, favouriteCelestialDataItem: FavouriteCelestialDataItem):
            ResourceUseCase<Boolean> {
        val resource = localDataRepository.saveBindingCelestialAndTagIntoDB(
            favouriteCelestialDataItem.celestialId,
            tagId
        )

        updateFavouriteDate(favouriteCelestialDataItem.nasaId)

        return if (resource is Resource.Success) ResourceUseCase.Success(true) else ResourceUseCase.Error(
            "${resource.message}"
        )
    }

    private suspend fun updateFavouriteDate(nasaId: String) {
        val resource = localDataRepository.getDateAsLongFavouriteCelestialByNasaId(nasaId)

        // if there is empty date then we save it as Now()
        if (resource is Resource.Error) {
            localDataRepository.updateDateFavouriteCelestialByNasaId(nasaId, Date().time)
        }
    }
}

