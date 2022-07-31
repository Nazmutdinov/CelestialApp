package com.example.celestialapp.domain.models

data class FavouriteCelestialDataItem(
    // id в локальной БД
    val celestialId: Int,
    val nasaId: String,
    val title: String,
    // сколько лет было сделано оригинальное фото
    val yearsAgo: String,

    // для упорядочивания фото по датам сохранения их в ибранных
    val orderDateCreated: Long? = null,
    val description: String,
    // medium картинка
    val image: ByteArray? = null,
    // ссылка на картинку для шаринга
    val imagePath: String
)
