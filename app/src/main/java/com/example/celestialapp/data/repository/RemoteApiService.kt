package com.example.celestialapp.data.repository

import com.example.celestialapp.data.Constants
import com.example.celestialapp.data.remote.detailedDto.DetailedInfoDto
import com.example.celestialapp.data.remote.generalDto.GeneralInfoDto
import com.example.celestialapp.data.remote.imageDto.ImageDataInfo
import retrofit2.http.*

interface RemoteApiService {
    /**
     * get basic data lists of celestials
     */
    @GET(Constants.apiGetPath)
    suspend fun getDataInfoDto(
        @Query("keywords") keywords: List<String>,
        @Query("page") page: Int
    ): GeneralInfoDto

    @GET(Constants.apiGetPath)
    suspend fun getDetailedDataInfoDto(
        @Query("nasa_id") nasaId: String,
    ): DetailedInfoDto

    @GET("asset/{nasa_id}")
    suspend fun getImageDataInfoDto(
        @Path("nasa_id") nasaId: String,
    ): ImageDataInfo
}