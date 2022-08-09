package com.example.celestialapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.celestialapp.R
import com.example.celestialapp.data.remote.detailedDto.DetailedInfoDto
import com.example.celestialapp.data.remote.imageDto.ImageDataInfo
import com.example.celestialapp.domain.repository.RemoteDataRepository


class RemoteDataRepositoryImpl(
    private val context: Context,
    private val remoteApiService: RemoteApiService,
    private val imageLoader: ImageLoader
    ) : RemoteDataRepository {

    override suspend fun getDetailedData(nasaId: String): Resource<DetailedInfoDto> {
        return try {
            val resource = remoteApiService.getDetailedDataInfoDto(nasaId)
            Resource.Success(resource)

        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun getImagePath(nasaId: String): Resource<ImageDataInfo> {
        return try {
                val resource = remoteApiService.getImageDataInfoDto(nasaId)
                Resource.Success(resource)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun getLargeImage(imagePath: String): Resource<Bitmap> {
        val request = ImageRequest.Builder(context)
            .data(imagePath)
            .build()

        val imageResult = imageLoader.execute(request)

        imageResult.drawable?.let { drawable ->
            return Resource.Success(drawable.toBitmap() )
        }

        return Resource.Error(context.getString(R.string.getLargeImageError))
    }
}