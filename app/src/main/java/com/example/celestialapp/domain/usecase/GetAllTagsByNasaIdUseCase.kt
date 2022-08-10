package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.LocalDataMapper
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.repository.LocalDataRepository

class GetAllTagsByNasaIdUseCase(
    private val localDataRepository: LocalDataRepository,
    private val localDataMapper: LocalDataMapper
) {
    suspend operator fun invoke(listNasaId: List<String>): ResourceUseCase<List<TagDataItem>> {
        val resourceWithCelestial = localDataRepository.getCelestialsWithTagsByListNasaId(listNasaId)
        val tagsWithCelestialData = resourceWithCelestial.data

        if (resourceWithCelestial is Resource.Success) {
            // get all tags, which have not in exclusive list
            tagsWithCelestialData?.let { tagWithCelestials ->
                val selectedTagInfoEntities = tagWithCelestials.flatMap { it.tagInfoEntity }
                // exclusive list tags with binding to celestials
                val selectedTagsId = selectedTagInfoEntities.map { it.tagId }.distinct()

                // get all tags but tags from exclusive list
                val resourceExclusive = localDataRepository.getExclusiveTags(selectedTagsId)
                val exclusiveTags = resourceExclusive.data

                if (resourceExclusive is Resource.Success) {
                    exclusiveTags?.let { unselectedTagInfoEntities ->
                        val selectedTags = selectedTagInfoEntities.map {
                            localDataMapper.getSelectedTagDataItem(it)
                        }

                        val unselectedTags = unselectedTagInfoEntities.map {
                             localDataMapper.getUnselectedTagDataItem(it)
                         }

                        return ResourceUseCase.Success(selectedTags + unselectedTags)
                    }
                }
            }
        }

        return ResourceUseCase.Error("${resourceWithCelestial.message}")
    }
}
