package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * получить список всех ключевых слов для заданного списка небесных тел
 */
class GetTagsByNasaIdUseCase(
    private val localDataRepository: LocalDataRepository,
) {
    suspend operator fun invoke(listNasaId: List<String>): ResourceUseCase<List<TagDataItem>> {
        // список ключевых слов с привязанными телами
        val resourceWithCelestial = localDataRepository.getDataByListNasaId(listNasaId)
        val tagsWithCelestialData = resourceWithCelestial.data

        if (resourceWithCelestial is Resource.Success) {
            // достанем получить все ключевые слова, которых нет в заданном списке исключения
            tagsWithCelestialData?.let { tagWithCelestials ->
                // ключи-исключения, которые имеют привязанные небесные тела
                val listTagInfoEntity = tagWithCelestials.flatMap { it.tagInfoEntity }
                val listTagId = listTagInfoEntity.map { it.tagId }.distinct()

                // получим все ключевые слова, кроме слов из списка
                val resourceExclusive = localDataRepository.getExclusiveTags(listTagId)
                val exclusiveTags = resourceExclusive.data

                if (resourceExclusive is Resource.Success) {
                    exclusiveTags?.let { normalKeywords ->
                        // составим список-модель
                        // избранные
                        val bindingItems = listTagInfoEntity.map {
                            TagDataItem(
                                tagId = it.tagId,
                                name = it.name,
                                selected = true
                            )
                        }

                        // обычные
                        val normalItems = normalKeywords.map {
                            TagDataItem(
                                tagId = it.tagId,
                                name = it.name,
                                selected = false
                            )
                        }

                        val resultItems = bindingItems + normalItems

                        return ResourceUseCase.Success(resultItems)
                    }
                }
            }

        }

        return ResourceUseCase.Error("${resourceWithCelestial.message}")
    }
}
