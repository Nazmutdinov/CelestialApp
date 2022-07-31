package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.repository.LocalDataRepository


/**
 * удалить ключевое слово по tagId
 */
class DeleteTagsDataUseCase(
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(
        tagId: Int,
    ): ResourceUseCase<Boolean> {
        // почистим привязки ключевого слова и небесных тел, если они есть
        val resource = localDataRepository.deleteTagData(tagId)

        if (resource is Resource.Success) {
            val data = resource.data

            data?.let {
                return ResourceUseCase.Success(it)
            }
        }

        return ResourceUseCase.Error("${resource.message}")
    }


}

