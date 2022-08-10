package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

class GetFavouriteCelestialsByTagUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(listTagId: List<Int>):
            ResourceUseCase<List<FavouriteCelestialDataItem>> {
        val resourceTagCelestials = localDataRepository.getTagsWithCelestialsByListTagId(listTagId)

        if (resourceTagCelestials is Resource.Success) {
            // it's cross ref table, so use flatMap
            resourceTagCelestials.data?.flatMap { tagWithCelestials ->
                tagWithCelestials.celestialInfoEntity
            }
                ?.distinctBy {it.nasaId}
                ?.sortedByDescending { it.dateFavouriteCreated }
                ?.let { celestials ->
                return ResourceUseCase.Success(
                    localDataMapper.mapListCelestialInfoEntityToModel(celestials)
                )
            }
        }

        return ResourceUseCase.Error("${resourceTagCelestials.message}")
    }
}