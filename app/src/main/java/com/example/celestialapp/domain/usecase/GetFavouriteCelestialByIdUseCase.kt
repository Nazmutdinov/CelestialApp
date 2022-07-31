package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * получить детальную инфу небесного тела из БД для заданного nasaId
 */
class GetFavouriteCelestialByIdUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(nasaId: String):
            ResourceUseCase<FavouriteCelestialDataItem> {
        val resourceCache = localDataRepository.getCelestialByNasaId(nasaId)

        // если данные есть в кэше, то вернем их
        if (resourceCache is Resource.Success) {
            // распакуем ключевые слова из кросс таблицы небесных тел и api ключевых слов
            resourceCache.data?.let {
                val result =
                    localDataMapper.mapCelestialInfoEntityToModel(it)

                return ResourceUseCase.Success(result)
            }
        }

        return ResourceUseCase.Error("${resourceCache.message}")
    }
}