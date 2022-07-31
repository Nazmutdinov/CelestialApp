package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository
import java.util.*

/**
 * привязать небесное тело к тегу
 */
class UpdateTagCelestialUseCase(
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(
        tagId: Int,
        // свойства небесного тела
        favouriteCelestialDataItem: FavouriteCelestialDataItem
    ): ResourceUseCase<Boolean> {
        // сохраним связь небесного тела и тега в БД
        val resource =
            localDataRepository.insertCelestialTagsCrossRef(favouriteCelestialDataItem.celestialId, tagId)

        // обновим дату сохранения тела в избранном
        updateFavouriteDate(favouriteCelestialDataItem.nasaId)

        return if (resource is Resource.Success) ResourceUseCase.Success(true) else ResourceUseCase.Error(
            "${resource.message}"
        )
    }

    /**
     * обновление даты сохранения тела в избранном
     */
    private suspend fun updateFavouriteDate(nasaId: String) {
        val resource =  localDataRepository.getFavouriteDate(nasaId)

        // дата пустая, то сохрани м текущую
        if (resource is Resource.Error) {
            localDataRepository.updateFavouriteDate(nasaId, Date().time)
        }
    }
}

