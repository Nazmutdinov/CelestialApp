package com.example.celestialapp.domain.usecase

import android.graphics.Bitmap
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * сохраним картинку из сети в БД
 */
class UpdateCacheUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper,
    private val utils: Utils
) {
    /**
     * извлечь данные о небесном теле
     * если данные есть в кэше, берем из кэша
     * если нет в кэше, берем из сети и сохраняем в кэш
     */
    suspend operator fun invoke(nasaId: String, bitmap: Bitmap): ResourceUseCase<FavouriteCelestialDataItem> {
        val image = utils.getByteArray(bitmap)

        val resource = localDataRepository.updateImageData(nasaId, image)

        // если картинка успшено обновленае, то вернем результат в сиде небесного тела
        if (resource is Resource.Success) {
            resource.data?.let { celestialInfoEntity ->
                val result = localDataMapper.mapCelestialInfoEntityToModel(celestialInfoEntity)
                return ResourceUseCase.Success(result)
            }
        }

        return ResourceUseCase.Error("${resource.message}")
    }
}