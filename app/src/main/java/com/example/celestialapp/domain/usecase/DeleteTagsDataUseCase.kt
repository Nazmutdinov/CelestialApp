package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.repository.LocalDataRepository

class DeleteTagsDataUseCase(
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(tagId: Int): ResourceUseCase<Boolean> {
        val resource = localDataRepository.deleteTagData(tagId)

        if (resource is Resource.Success) {
            resource.data?.let { result ->
                return ResourceUseCase.Success(result)
            }
        }

        return ResourceUseCase.Error("${resource.message}")
    }
}

