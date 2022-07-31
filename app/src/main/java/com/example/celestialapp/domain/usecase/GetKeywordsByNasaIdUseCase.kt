package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * получить список привязки ключевых слов для заданного nasaId
 */
class GetKeywordsByNasaIdUseCase(
    private val localDataRepository: LocalDataRepository

) {
    suspend operator fun invoke(nasaId: String): ResourceUseCase<List<String>> {
        val resourceCelestialWithKeywords = localDataRepository.getKeywordsByNasaId(nasaId)

        if (resourceCelestialWithKeywords is Resource.Success) {
            val listCelestialWithKeywords = resourceCelestialWithKeywords.data

            val data = listCelestialWithKeywords
                ?.flatMap { celestialWithKeywords ->
                celestialWithKeywords.keywordInfoEntity}
                ?.map { keywordInfoEntity ->
                    keywordInfoEntity.name
                }
                ?.distinct()

            data?.let {
                return ResourceUseCase.Success(it)
            }
        }

        return ResourceUseCase.Error("${resourceCelestialWithKeywords.message}")
    }
}