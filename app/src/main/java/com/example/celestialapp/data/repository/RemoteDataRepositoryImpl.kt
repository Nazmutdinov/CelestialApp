package com.example.celestialapp.data.repository

import com.example.celestialapp.data.remote.detailedDto.DetailedInfoDto
import com.example.celestialapp.data.remote.imageDto.ImageDataInfo
import com.example.celestialapp.domain.repository.RemoteDataRepository


class RemoteDataRepositoryImpl(
    private val remoteApiService: RemoteApiService
    ) : RemoteDataRepository {
    /**
     * подробные данные по небесному телу nasa_id
     */
    override suspend fun getDetailedData(nasaId: String): Resource<DetailedInfoDto> {
        return try {
            val resource = remoteApiService.getDetailedDataInfoDto(nasaId)
            Resource.Success(resource)

        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    /**
     * ссылка на json с картинками medium и thumbnail
     */
    override suspend fun getImagePath(nasaId: String): Resource<ImageDataInfo> {
        return try {
                val resource = remoteApiService.getImageDataInfoDto(nasaId)
                Resource.Success(resource)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }
}