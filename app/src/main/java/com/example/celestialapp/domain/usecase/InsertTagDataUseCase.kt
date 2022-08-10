package com.example.celestialapp.domain.usecase

import android.content.Context
import com.example.celestialapp.R
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * save new tag into DB
 */
class InsertTagDataUseCase(
    private val context: Context,
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(tagName: String): ResourceUseCase<Boolean> {
        val existsTags = isTagExisting(tagName)

        return if (existsTags) ResourceUseCase.Error(context.getString(R.string.insertKeywordDataUseCaseError))
        else {
            val resource = localDataRepository.saveTagIntoDBAndGetTagId(tagName)

            if (resource is Resource.Success) ResourceUseCase.Success(true) else
                ResourceUseCase.Error("${resource.message}")
        }
    }

    private suspend fun isTagExisting(name: String): Boolean {
        val existsTags = localDataRepository.getAllTags()

        return existsTags.data?.map { it.name }?.contains(name) ?: false
    }
}

