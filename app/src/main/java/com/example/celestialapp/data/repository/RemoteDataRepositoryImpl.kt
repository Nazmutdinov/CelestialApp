package com.example.celestialapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
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

    override suspend fun getLargeImage(imagePath: String, callback: (Resource<Bitmap>) -> Unit) {
        val request = ImageRequest.Builder(context)
            .data(imagePath)
            .target(
                onStart = {
                          Log.d("myTag","start loading")
                },
                onSuccess = {
                    Log.d("myTag","loaded!")
                    callback( Resource.Success(it.toBitmap()))
                },
                onError = {
                    Log.d("myTag","error")
                    callback(Resource.Error(context.getString(R.string.getLargeImageError)))
                }
            )
            .build()

        imageLoader.execute(request)
    }
}