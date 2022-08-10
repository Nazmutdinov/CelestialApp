package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

class GetFavouriteCelestialByIdUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(nasaId: String): ResourceUseCase<FavouriteCelestialDataItem> {
        val resourceCache = localDataRepository.getCelestialByNasaId(nasaId)

        if (resourceCache is Resource.Success) {
            resourceCache.data?.let { celestialInfoEntity ->
                val result = localDataMapper.mapCelestialInfoEntityToModel(celestialInfoEntity)
                return ResourceUseCase.Success(result)
            }
        }

        return ResourceUseCase.Error("${resourceCache.message}")
    }
}