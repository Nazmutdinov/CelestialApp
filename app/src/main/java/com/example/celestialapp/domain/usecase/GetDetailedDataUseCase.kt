package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.remote.imageDto.ImageDataInfo
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository
import com.example.celestialapp.domain.repository.RemoteDataRepository

/**
 * получить детальную инфу о небесном теле
 * извлечь данные о небесном теле
 * если данные есть в кэше, берем из кэша
 * если нет в кэше, берем из сети и сохраняем в кэш
 */
class GetDetailedDataUseCase(
    private val localDataRepository: LocalDataRepository,
    private val remoteDataRepository: RemoteDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(nasaId: String): ResourceUseCase<FavouriteCelestialDataItem> {
        // если данных нет в кэше, то загрузим из api
        val resourceDetailedData = remoteDataRepository.getDetailedData(nasaId)
        val resourceImageData = remoteDataRepository.getImagePath(nasaId)

        if (resourceDetailedData is Resource.Success && resourceImageData is Resource.Success) {
            val detailedDataItem =
                resourceDetailedData.data?.collection?.items?.first()?.data?.first()

            // путь к картинке original
            val imagePath = getImagePath(resourceImageData)

            if (detailedDataItem != null && imagePath != null) {
                //  сохраним в базе кэш без картинки
                // картинка позже скачается и дополнит кэш
                val resourceCelestialInfoEntity = localDataRepository.insertCelestialData(
                    nasaId = detailedDataItem.nasa_id,
                    title = detailedDataItem.title,
                    date = detailedDataItem.date_created,
                    description = detailedDataItem.description,
                    image = null,
                    imagePath
                )

                val keywords = detailedDataItem.keywords

                if (resourceCelestialInfoEntity is Resource.Success) {
                    resourceCelestialInfoEntity.data?.let { celestialInfoEntity ->
                        // сохраним api ключевые слова в БД
                        saveKeywords(celestialInfoEntity.celestialId, keywords)

                        val favouriteCelestialDataItem =
                            localDataMapper.mapCelestialInfoEntityToModel(celestialInfoEntity)
                        return ResourceUseCase.Success(favouriteCelestialDataItem)
                    }
                }

                return ResourceUseCase.Error("${resourceCelestialInfoEntity.message}")
            }
        }

        return ResourceUseCase.Error("${resourceDetailedData.message} ${resourceImageData.message}")
    }

    /**
     * сохранить привязку ключевых слов к небесному телу в БД
     */
    private suspend fun saveKeywords(celestialId: Int, keywordNames: List<String>) {
        keywordNames.forEach { keywordName ->
            // ключевые слова могут быть уже в базе
            val resourceKeyword = localDataRepository.getKeywordsByName(keywordName)

            if (resourceKeyword is Resource.Success) {
                // такое ключевое слово уже есть в БД
                resourceKeyword.data?.keywordId?.let { keywordId ->
                    // сохраним привязку ключевого слова к телу в БД
                    localDataRepository.insertCelestialKeywordsCrossRef(celestialId, keywordId)
                }
            } else {
                // нужно создать ключевое слово в БД
                val resourceNewKeyword = localDataRepository.insertKeywordData(keywordName)

                if (resourceNewKeyword is Resource.Success) {
                    resourceNewKeyword.data?.let { keywordId ->
                        // сохраним привязку ключевого слова к телу в БД
                        localDataRepository.insertCelestialKeywordsCrossRef(celestialId, keywordId)
                    }
                }
            }
        }
    }

    /**
     * получить путь к картинке
     */
    private fun getImagePath(resource: Resource<ImageDataInfo>): String? {
        val collectionPaths = resource.data?.collection?.items

        return collectionPaths?.find { item ->
            item.href.contains("~orig")
        }?.href
    }
}