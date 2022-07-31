package com.example.celestialapp.domain.usecase

import android.content.Context
import com.example.celestialapp.R
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.repository.LocalDataRepository

/**
 * добавить новое ключевое слово
 */
class InsertTagDataUseCase(
    private val context: Context,
    private val localDataRepository: LocalDataRepository
) {
    /**
     * сохранить ключевое слово
     */
    suspend operator fun invoke(tagName: String): ResourceUseCase<Boolean> {
        val existsTags = isTagExisting(tagName)

        return if (existsTags) ResourceUseCase.Error(context.getString(R.string.insertKeywordDataUseCaseError))
        else {
            // такого тэга еще нет в БД
            val resource = localDataRepository.insertTagData(tagName)

            if (resource is Resource.Success) ResourceUseCase.Success(true) else
                ResourceUseCase.Error("${resource.message}")
        }
    }

    /**
     *проверяем, есть уже такой тэг?
     */
    private suspend fun isTagExisting(name: String): Boolean {
        val existsTags = localDataRepository.getTags()

        return existsTags.data?.map { it.name }?.contains(name) ?: false
    }
}

