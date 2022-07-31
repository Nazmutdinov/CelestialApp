package com.example.celestialapp.domain.repository

import com.example.celestialapp.data.local.entities.*
import com.example.celestialapp.data.local.relations.CelestialWithKeywords
import com.example.celestialapp.data.local.relations.CelestialWithTags
import com.example.celestialapp.data.local.relations.TagWithCelestials
import com.example.celestialapp.data.repository.Resource

// интерфейс к локальной БД
interface LocalDataRepository {
    /**
     * достать все небесные тела и теги для заданного списка nasaId небесных тел
     */
    suspend fun getDataByListNasaId(listNasaId: List<String>): Resource<List<CelestialWithTags>>

    /**
     * достать все небесные тела и теги для заданного списка ключевых слов
     */
    suspend fun getDataByListTagId(listTagId: List<Int>): Resource<List<TagWithCelestials>>

    /**
     * достать все теги из БД
     */
    suspend fun getTags(): Resource<List<TagInfoEntity>>

    /**
     * достать все теги, которых нет в списке listTagId
     */
    suspend fun getExclusiveTags(listTagId: List<Int>): Resource<List<TagInfoEntity>>

    /**
     * достать ссылку на тег в БД
     */
    suspend fun getTagIdByName(tagName: String): Resource<Int>

    /**
     * достать ссылку на небесное тело в БД
     */
    suspend fun getCelestialByNasaId(nasaId: String): Resource<CelestialInfoEntity>

    /**
     * достать все небесные тела и api ключевые словаи для заданного nasaId
     */
    suspend fun getKeywordsByNasaId(nasaId: String): Resource<List<CelestialWithKeywords>>

    /**
     * достать api ключевое слово для заданного keywordName
     */
    suspend fun getKeywordsByName(keywordName: String): Resource<KeywordInfoEntity>

    /**
     * достать дату сохранения небесного тела в избранное
     */
    suspend fun getFavouriteDate(nasaId: String): Resource<Long>



    /**
     * сохранить данные по небесному телу в БД
     */
    suspend fun insertCelestialData(
        nasaId: String,
        title: String,
        date: String,
        description: String,
        image: ByteArray?,
        imagePath: String
    ): Resource<CelestialInfoEntity>

    /**
     * сохранить картинку небесноого тела в БД
     */
    suspend fun updateImageData(
        nasaId: String,
        image: ByteArray
    ): Resource<CelestialInfoEntity>

    /**
     * сохранить дату избранного небесноого тела в БД
     */
    suspend fun updateFavouriteDate(
        nasaId: String,
        dateFavouriteCreated: Long?
    ): Resource<CelestialInfoEntity>

    /**
     * сохранить данные по тегу в БД, получить tagId
     */
    suspend fun insertTagData(tagName: String): Resource<Int>

    /**
     * сохранить привязку тега и небесного тела в БД
     */
    suspend fun insertCelestialTagsCrossRef(
        celestialId: Int,
        tagId: Int
    ): Resource<CelestialTagCrossRef>

    /**
     * сохранить данные по api ключевому слово в БД, получить keywordId
     */
    suspend fun insertKeywordData(keywordName: String): Resource<Int>

    /**
     * сохранить привязку api ключевого слова и небесного тела в БД
     */
    suspend fun insertCelestialKeywordsCrossRef(
        celestialId: Int,
        keywordId: Int
    ): Resource<CelestialKeywordCrossRef>

    /**
     * удалить тег
     */
    suspend fun deleteTagData(tagId: Int): Resource<Boolean>

    /**
     * удалить привязку тега и небесного тела в БД
     */
    suspend fun deleteCelestialTagsCrossRef(celestialId: Int, tagId: Int): Resource<Boolean>
}