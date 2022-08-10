package com.example.celestialapp.domain.usecase

import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.repository.LocalDataRepository

class GetKeywordsByNasaIdUseCase(private val localDataRepository: LocalDataRepository) {
    suspend operator fun invoke(nasaId: String): ResourceUseCase<List<String>> {
        val resourceCelestialWithKeywords = localDataRepository.getKeywordsByNasaId(nasaId)

        if (resourceCelestialWithKeywords is Resource.Success) {
            resourceCelestialWithKeywords.data?.let { listOfCelestialWithKeywords ->
                listOfCelestialWithKeywords.flatMap { celestialWithKeywords ->
                    celestialWithKeywords.keywordInfoEntity
                }
                    .map { keywordInfoEntity ->
                        keywordInfoEntity.name
                    }
                    .distinct()
                    .let { listKeywordNames ->
                        return ResourceUseCase.Success(listKeywordNames)
                    }
            }
        }

        return ResourceUseCase.Error("${resourceCelestialWithKeywords.message}")
    }
}