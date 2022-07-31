package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * получить список всех ключевых слов какие есть в БД
 */
class GetAllTagsUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(): ResourceUseCase<List<TagDataItem>> {
        val resource = localDataRepository.getTags()

        if (resource is Resource.Success) {
            resource.data?.let { list ->
                // сортировка ключевых слов по имени
                list.sortedBy { tagInfoEntity ->
                    tagInfoEntity.name
                }.map {
                    // маппим ключевое слово из БД в модель
                    localDataMapper.mapTagInfoEntityToModel(it)
                }.let { resultList ->
                    return ResourceUseCase.Success(resultList)
                }
            }
        }

        return ResourceUseCase.Error("${resource.message}")
    }
}
