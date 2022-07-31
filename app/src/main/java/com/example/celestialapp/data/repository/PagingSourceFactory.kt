package com.example.celestialapp.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.celestialapp.data.remote.generalDto.Item
import com.example.celestialapp.domain.models.CelestialDataItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PagingSourceFactory(
    private val keywords: List<String>,
    private val dispatcher: CoroutineDispatcher,
    private val remoteApiService:  RemoteApiService,
    private val remoteDataMapper: RemoteDataMapper
) : PagingSource<Int, CelestialDataItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CelestialDataItem> {
        return try {
            withContext(dispatcher) {
                val pageIndex = params.key ?: 1
                val dataInfoDto = remoteApiService.getDataInfoDto(keywords, pageIndex)

                val data = dataInfoDto.collection.items.filter { item ->
                    !isDataItemEmpty(item)
                }.map { item ->
                    remoteDataMapper.mapDtoToModel(item, false)
                }

                if (data.isEmpty()) return@withContext LoadResult.Error(throwable = Exception())
                return@withContext LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = pageIndex + 1
                )
            }
        } catch (e: Exception) {
            Log.e("myTag", "${e.stackTrace} ${e.message}")
            LoadResult.Error(throwable = e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CelestialDataItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /**
     * берем только непустые данные
     */
    private fun isDataItemEmpty(item: Item): Boolean {
        val title = item.data.first().title
        val image = item.links.first().href

        return (title == null || image == null)
    }
}