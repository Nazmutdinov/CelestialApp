package com.example.celestialapp.domain.usecase

sealed class ResourceUseCase<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): ResourceUseCase<T>(data)
    class Error<T>(message: String, data: T? = null): ResourceUseCase<T>(data ,message)
}
