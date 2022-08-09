package com.example.celestialapp.domain.usecase

import android.graphics.Bitmap
import com.example.celestialapp.data.local.entities.CelestialInfoEntity
import com.example.celestialapp.data.local.entities.KeywordInfoEntity
import com.example.celestialapp.data.remote.detailedDto.Data
import com.example.celestialapp.data.remote.imageDto.ImageDataInfo
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository
import com.example.celestialapp.domain.repository.RemoteDataRepository

/**
 * get detailed data about celestial: description, large image
 * save it into cache
 */
class GetDetailedDataUseCase(
    private val localDataRepository: LocalDataRepository,
    private val remoteDataRepository: RemoteDataRepository,
    private val localDataMapper: LocalDataMapper,
    private val utils: Utils
) {
    suspend operator fun invoke(nasaId: String): ResourceUseCase<FavouriteCelestialDataItem> {
        val resourceDetailedData = remoteDataRepository.getDetailedData(nasaId)
        val resourceImageData = remoteDataRepository.getImagePath(nasaId)

        if (resourceDetailedData is Resource.Success && resourceImageData is Resource.Success) {
            val detailedDataItem =
                resourceDetailedData.data?.collection?.items?.first()?.data?.first()

            val imagePath = getImagePath(resourceImageData)

            detailedDataItem?.let { data ->
                imagePath?.let {  path ->
                    return saveCelestialDataIntoDb( data, path)
                }
            }
        }

        return ResourceUseCase.Error("shit !${resourceDetailedData.message} ${resourceImageData.message}")
    }

    private suspend fun saveCelestialDataIntoDb(
        detailedDataItem: Data, imagePath: String
    ): ResourceUseCase<FavouriteCelestialDataItem> {
        val keywords = detailedDataItem.keywords
        val resourceImage = remoteDataRepository.getLargeImage(imagePath)
        val resourceCelestialInfoEntity = insertCelestialData(detailedDataItem, imagePath, resourceImage.data)

        if (resourceCelestialInfoEntity is Resource.Success) {
            resourceCelestialInfoEntity.data?.let { celestialInfoEntity ->
                saveBindingKeywordsAndCelestialIntoDb(
                    celestialInfoEntity.celestialId,
                    keywords
                )

                val favouriteCelestialDataItem =
                    localDataMapper.mapCelestialInfoEntityToModel(celestialInfoEntity)

                return ResourceUseCase.Success(favouriteCelestialDataItem)
            }
        }

        return ResourceUseCase.Error("${resourceCelestialInfoEntity.message}")
    }


    private suspend fun insertCelestialData(
        detailedDataItem: Data,
        imagePath: String,
        image: Bitmap?
    ): Resource<CelestialInfoEntity> = localDataRepository.insertCelestialData(
        nasaId = detailedDataItem.nasa_id,
        title = detailedDataItem.title,
        date = detailedDataItem.date_created,
        description = detailedDataItem.description,
        image = if (image != null) utils.getByteArray(image) else null,
        imagePath = imagePath
    )

    private suspend fun saveBindingKeywordsAndCelestialIntoDb(
        celestialId: Int,
        keywordNames: List<String>
    ) {
        keywordNames.forEach { keywordName ->
            val resourceKeyword = localDataRepository.getKeywordsByName(keywordName)

            if (resourceKeyword is Resource.Success) {
                // is there keyword at cache?
                insertBindCelestialAndKeyword(celestialId, resourceKeyword, null)
            } else {
                // there is no keyword in DB yet
                // therefore we need save it into DB
                val resourceNewKeyword = localDataRepository.insertKeywordData(keywordName)
                insertBindCelestialAndKeyword(celestialId, null, resourceNewKeyword)
            }
        }
    }

    private suspend fun insertBindCelestialAndKeyword(
        celestialId: Int,
        resourceKeywordInfoEntity: Resource<KeywordInfoEntity>?,
        resourceKeywordId: Resource<Int>?
    ) {
        // either keyword already exists
        resourceKeywordInfoEntity?.data?.keywordId?.let { keywordId ->
            localDataRepository.insertCelestialKeywordsCrossRef(celestialId, keywordId)
        }

        // either keyword has been created and this it's id
        resourceKeywordId?.data?.let { keywordId ->
            localDataRepository.insertCelestialKeywordsCrossRef(celestialId, keywordId)
        }
    }

    private fun getImagePath(resource: Resource<ImageDataInfo>): String? {
        val collectionPaths = resource.data?.collection?.items

        return collectionPaths?.find { item ->
            item.href.contains("~orig")
        }?.href
    }
}