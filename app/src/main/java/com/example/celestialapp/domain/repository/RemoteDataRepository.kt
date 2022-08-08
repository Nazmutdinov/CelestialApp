package com.example.celestialapp.domain.repository

import android.graphics.Bitmap
import com.example.celestialapp.data.remote.detailedDto.DetailedInfoDto
import com.example.celestialapp.data.remote.imageDto.ImageDataInfo
import com.example.celestialapp.data.repository.Resource

// интерфейс к удаленному API
interface RemoteDataRepository {
    /**
     * подробные данные по небесному телу nasa_id
     */
    suspend fun getDetailedData(nasaId: String): Resource<DetailedInfoDto>

    /**
     * ссылка на json с картинками
     */
    suspend fun getImagePath(nasaId: String): Resource<ImageDataInfo>

    suspend fun getLargeImage(imagePath: String, callback: (Resource<Bitmap>) -> Unit)
}


