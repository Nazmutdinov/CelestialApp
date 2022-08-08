package com.example.celestialapp.domain.usecase

import android.graphics.Bitmap
import com.example.celestialapp.data.repository.Resource
import com.example.celestialapp.domain.repository.RemoteDataRepository

class GetLargeImageUseCase(
    private val remoteDataRepository: RemoteDataRepository,
) {

    suspend operator fun invoke(imagePath: String, callback: (ResourceUseCase<Bitmap>) -> Unit) {
        remoteDataRepository.getLargeImage(imagePath) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        callback(ResourceUseCase.Success(resource.data))
                    }
                }
                is Resource.Error -> callback(ResourceUseCase.Error(resource.message ?: ""))
            }
        }
    }
}