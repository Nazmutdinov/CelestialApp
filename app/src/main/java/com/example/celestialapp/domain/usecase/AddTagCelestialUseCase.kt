package com.example.celestialapp.domain.usecase

import android.content.Context
import com.example.celestialapp.R
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository
import java.util.*

/**
 * save tag into db, save binding tag to celestial into db
 */
class AddTagCelestialUseCase(
    private val context: Context,
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(
        tagName: String,
        favouriteCelestialDataItem: FavouriteCelestialDataItem
    ): ResourceUseCase<Boolean> {
        val existsTags = isTagExistingInDb(tagName)
        if (existsTags) return ResourceUseCase.Error(context.getString(R.string.insertKeywordDataUseCaseError))

        // if there is not this tag at db, then add tag
        val resourceInsertTag = localDataRepository.insertTagData(tagName)
        if (resourceInsertTag is Resource.Error) return ResourceUseCase.Error("${resourceInsertTag.message}")

        resourceInsertTag.data?.let { keywordId ->
            val resourceCrossRef =
                localDataRepository.insertCelestialTagsCrossRef(
                    favouriteCelestialDataItem.celestialId,
                    keywordId
                )

            updateDateForCelestialIntoDb(favouriteCelestialDataItem.nasaId)

            return if (resourceCrossRef is Resource.Success) ResourceUseCase.Success(true)
            else ResourceUseCase.Error("${resourceCrossRef.message}")
        }

        return ResourceUseCase.Error(context.getString(R.string.nonDefineError))
    }

    private suspend fun isTagExistingInDb(tagName: String): Boolean {
        val tagId = localDataRepository.getTagIdByName(tagName).data

        return tagId != null
    }


    private suspend fun updateDateForCelestialIntoDb(nasaId: String) {
        val resource = localDataRepository.getFavouriteDate(nasaId)

        // if there in no date in db, then save current date into db
        if (resource is Resource.Error) localDataRepository.updateFavouriteDate(nasaId, Date().time)
    }
}

