package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * получить список небесных тел для заданного списка тегов
 */
class GetFavouriteCelestialsByTagUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(listTagId: List<Int>):
            ResourceUseCase<List<FavouriteCelestialDataItem>> {
        val resourceTagCelestials = localDataRepository.getDataByListTagId(listTagId)

        if (resourceTagCelestials is Resource.Success) {
            // распакуем кросс таблицу
            resourceTagCelestials.data?.flatMap { tagWithCelestials ->
                tagWithCelestials.celestialInfoEntity
            }
                ?.distinctBy {it.nasaId}
                ?.sortedByDescending { it.dateFavouriteCreated }
                ?.let { celestials ->
                return ResourceUseCase.Success(
                    localDataMapper.mapListCelestialInfoEntityToModel(
                        celestials
                    )
                )
            }
        }

        return ResourceUseCase.Error("${resourceTagCelestials.message}")
    }
}