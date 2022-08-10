package com.example.celestialapp.domain.repository

import com.example.celestialapp.data.local.entities.*
import com.example.celestialapp.data.local.relations.CelestialWithKeywords
import com.example.celestialapp.data.local.relations.CelestialWithTags
import com.example.celestialapp.data.local.relations.TagWithCelestials
import com.example.celestialapp.data.repository.Resource

interface LocalDataRepository {
    // get operations
    suspend fun getCelestialsWithTagsByListNasaId(listNasaId: List<String>): Resource<List<CelestialWithTags>>
    suspend fun getTagsWithCelestialsByListTagId(listTagId: List<Int>): Resource<List<TagWithCelestials>>
    suspend fun getAllTags(): Resource<List<TagInfoEntity>>

    /**
     * get all tags BUT in listTagId
     */
    suspend fun getExclusiveTags(listTagId: List<Int>): Resource<List<TagInfoEntity>>
    suspend fun getTagIdByName(tagName: String): Resource<Int>
    suspend fun getCelestialByNasaId(nasaId: String): Resource<CelestialInfoEntity>
    suspend fun getCelestialsWithKeywordsByNasaId(nasaId: String): Resource<List<CelestialWithKeywords>>
    suspend fun getApiKeywordsByName(keywordName: String): Resource<KeywordInfoEntity>
    suspend fun getDateAsLongFavouriteCelestialByNasaId(nasaId: String): Resource<Long>

    // save operations
    suspend fun saveCelestialDataIntoDB(
        nasaId: String,
        title: String,
        date: String,
        description: String,
        image: ByteArray?,
        imagePath: String
    ): Resource<CelestialInfoEntity>

    suspend fun updateDateFavouriteCelestialByNasaId(
        nasaId: String,
        dateFavouriteCreated: Long?
    ): Resource<CelestialInfoEntity>

    suspend fun saveTagIntoDBAndGetTagId(tagName: String): Resource<Int>
    suspend fun saveBindingCelestialAndTagIntoDB(
        celestialId: Int,
        tagId: Int
    ): Resource<CelestialTagCrossRef>

    suspend fun saveApiKeywordIntoDBAndGetId(keywordName: String): Resource<Int>
    suspend fun saveBindingCelestialAndKeywordIntoDB(
        celestialId: Int,
        keywordId: Int
    ): Resource<CelestialKeywordCrossRef>

    // delete operations
    suspend fun deleteTagById(tagId: Int): Resource<Boolean>
    suspend fun deleteBindingCelestialAndTag(celestialId: Int, tagId: Int): Resource<Boolean>
}
