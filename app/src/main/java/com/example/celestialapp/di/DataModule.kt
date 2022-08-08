package com.example.celestialapp.di

import android.content.Context
import coil.ImageLoader
import com.example.celestialapp.data.local.CelestialDatabase
import com.example.celestialapp.data.repository.*
import com.example.celestialapp.domain.repository.LocalDataRepository
import com.example.celestialapp.domain.repository.RemoteDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideCelestialDatabase(@ApplicationContext context: Context): CelestialDatabase =
        CelestialDatabase.getDatabase(context)


    @Provides
    @Singleton
    fun provideLocalDataRepository(
        @ApplicationContext context: Context,
        db: CelestialDatabase,
        dispatcher: CoroutineDispatcher
    ): LocalDataRepository = LocalDataRepositoryImpl(
        context,
        db,
        dispatcher
    )

    @Provides
    @Singleton
    fun provideRemoteDataRepository(
        @ApplicationContext context: Context,
        remoteApiService: RemoteApiService,
        imageLoader: ImageLoader
    ): RemoteDataRepository =
        RemoteDataRepositoryImpl(context, remoteApiService, imageLoader)

    @Provides
    @Singleton
    fun provideLocalDataMapper(utils: Utils): LocalDataMapper = LocalDataMapper(utils)


    @Provides
    @Singleton
    fun provideMyPager(
        dispatcher: CoroutineDispatcher,
        remoteApiService: RemoteApiService,
        remoteDataMapper: RemoteDataMapper
    ): MyPager = MyPager(dispatcher, remoteApiService, remoteDataMapper)

    @Provides
    @Singleton
    fun providePagingSourceFactory(
        keywords: List<String>,
        dispatcher: CoroutineDispatcher,
        remoteApiService: RemoteApiService,
        remoteDataMapper: RemoteDataMapper
    ): PagingSourceFactory =
        PagingSourceFactory(keywords, dispatcher, remoteApiService, remoteDataMapper)

    @Provides
    @Singleton
    fun provideRemoteDataMapper(): RemoteDataMapper = RemoteDataMapper()


    // всмомогательные улилиты
    @Provides
    @Singleton
    fun provideUtils(@ApplicationContext context: Context): Utils = Utils(context)


}