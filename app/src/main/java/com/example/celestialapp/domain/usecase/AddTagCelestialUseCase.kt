package com.example.celestialapp.domain.usecase

import android.content.Context
import com.example.celestialapp.R
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository
import java.util.*

/**
 * сохранить привязку небесного тела к тегу, сохранить тег в БД
 */
class AddTagCelestialUseCase(
    private val context: Context,
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(
        // имя тега
        tagName: String,
        // id небесного тела
        favouriteCelestialDataItem: FavouriteCelestialDataItem
    ): ResourceUseCase<Boolean> {
        // проверим есть ли в БД такой же тег?
        val existsTags = isTagExisting(tagName)

        if (existsTags) return ResourceUseCase.Error(context.getString(R.string.insertKeywordDataUseCaseError))

        // такого тега еще нет в БД, добавим тег
        val resourceInsertTag = localDataRepository.insertTagData(tagName)

        if (resourceInsertTag is Resource.Error) return ResourceUseCase.Error("${resourceInsertTag.message}")
        // тег успешно добавлен, достанем keyword id
        resourceInsertTag.data?.let { keywordId ->
            // сохраним привязку небесного тела и тега в БД
            val resource =
                localDataRepository.insertCelestialTagsCrossRef(
                    favouriteCelestialDataItem.celestialId,
                    keywordId
                )

            // обновим дату сохранения тела в избранном
            updateFavouriteDate(favouriteCelestialDataItem.nasaId)

            return if (resource is Resource.Success) ResourceUseCase.Success(true)
                else ResourceUseCase.Error("${resource.message}"
            )
        }

        return ResourceUseCase.Error(context.getString(R.string.nonDefineError))
    }

    /**
     *проверяем, есть уже такой тег в БД
     */
    private suspend fun isTagExisting(tagName: String): Boolean {
        val tagId = localDataRepository.getTagIdByName(tagName)

        return tagId.data != null
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

