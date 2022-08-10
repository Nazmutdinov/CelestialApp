package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * get tags from local DB sorted by name
 */
class GetAllTagsUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(): ResourceUseCase<List<TagDataItem>> {
        val resource = localDataRepository.getAllTags()

        if (resource is Resource.Success) {
            resource.data?.let { list ->
                list.sortedBy { tagInfoEntity ->
                    tagInfoEntity.name
                }.map { localDataMapper.getSelectedTagDataItem(it)
                }.let { listOfTagDataItem ->
                    return ResourceUseCase.Success(listOfTagDataItem)
                }
            }
        }

        return ResourceUseCase.Error("${resource.message}")
    }
}
