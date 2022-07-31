package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * удалить привязку ключевого слова и небесного тела
 */
class DeleteCrossRefDataUseCase(
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(
        tagId: Int,
        celestialId: Int
    ): ResourceUseCase<Boolean> {
        // пробуем удалить привязку тега и тела
        val resource = localDataRepository.deleteCelestialTagsCrossRef(celestialId, tagId)

        if (resource is Resource.Success) {
            resource.data?.let {
                return ResourceUseCase.Success(it)
            }
        }

        return ResourceUseCase.Error("${resource.message}")
    }
}

