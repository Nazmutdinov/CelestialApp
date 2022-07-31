package com.example.celestialapp.data.repository

import androidx.paging.*
import com.example.celestialapp.domain.models.CelestialDataItem
import kotlinx.coroutines.CoroutineDispatcher

// получить все небесные тела из api
class MyPager(
    private val dispatcher: CoroutineDispatcher,
    //private val remoteApi: RemoteApi,
    private val remoteApiService: RemoteApiService,
    private val remoteDataMapper: RemoteDataMapper
) {
    /**
     * извлечь данные о небесном теле
     */
    operator fun invoke(
        keywords: List<String>,
        pageSize: Int
    ): Pager<Int, CelestialDataItem> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceFactory(keywords, dispatcher, remoteApiService, remoteDataMapper)
            }
        )
}