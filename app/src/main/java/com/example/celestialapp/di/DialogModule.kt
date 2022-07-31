package com.example.celestialapp.di

import com.example.celestialapp.presentation.fragments.DialogFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DialogModule {
    @Provides
    @Singleton
    fun provideDialogFactory(): DialogFactory =
        DialogFactory()
}