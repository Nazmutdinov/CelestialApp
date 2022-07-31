package com.example.celestialapp.data.local

import androidx.room.*
import com.example.celestialapp.data.local.entities.*
import com.example.celestialapp.data.local.relations.CelestialWithKeywords
import com.example.celestialapp.data.local.relations.CelestialWithTags
import com.example.celestialapp.data.local.relations.TagWithCelestials

@Dao
interface CelestialInfoDao {
    // блок выборок
    /**
     * проверить есть ли небесное тело в списке избранных для данного nasaId
     */
    @Query("SELECT * FROM celestialinfoentity WHERE nasa_id = :nasaId")
    fun getCelestialByNasaId(nasaId: String): CelestialInfoEntity?

    /**
     * получить все небесные тела
     */
    /*
    @Query("SELECT * FROM celestialinfoentity")
    fun getCelestials(): List<CelestialInfoEntity>

     */

    /**
     * получить небесное тело по celestialId
     */
    /*
    @Query("SELECT * FROM celestialinfoentity WHERE celestialId = :celestialId")
    fun getCelestialById(celestialId: Int): CelestialInfoEntity?

     */

    /**
     * получить все теги, которых нет в заданном списке исключения
     */
    @Query("SELECT * FROM taginfoentity WHERE tagId not IN (:listTagId)")
    fun getExclusiveTags(listTagId: List<Int>): List<TagInfoEntity>

    /**
     * получить все теги
     */
    @Query("SELECT * FROM taginfoentity")
    fun getTags(): List<TagInfoEntity>

    /**
     * получить id тега по имени
     */
    @Query("SELECT tagId FROM taginfoentity WHERE name = :tagName")
    fun getTagIdByName(tagName: String): Int?

    /**
     * получить все теги с привязкой к небесным телам для заданного списка nasaId
     */
    @Transaction
    @Query("SELECT * FROM celestialinfoentity WHERE nasa_id IN(:listNasaId)")
    fun getCelestialWithTags(listNasaId: List<String>): List<CelestialWithTags>

    /**
     * получить все теги с привязкой к небесным телам для заданного списка celestiaId
     */
    /*
    @Transaction
    @Query("SELECT * FROM celestialinfoentity WHERE celestialId IN(:listCelestialId)")
    fun getTagsByListCelestialId(listCelestialId: List<Int>): List<CelestialWithTags>

     */

    /**
     * получить все теги с привязкой к небесным телам для заданного celestiaId
     */
    /*
    @Query("SELECT celestialId FROM celestialtagcrossref WHERE celestialId = :celestialId")
    fun getCelestiaIdByCelestialId(celestialId: Int): List<Int>

     */

    /**
     * получить все привязки тел\тегов для заданного списка keywordId
     */
    @Transaction
    @Query("SELECT * FROM taginfoentity WHERE tagId IN(:listTagId)")
    fun getTagsByListTagId(listTagId: List<Int>): List<TagWithCelestials>

    /**
     * получить список всех тегов с привязкой к небесным телам
     */
    /*
    @Transaction
    @Query("SELECT * FROM taginfoentity ")
    fun getFavouriteTags(): List<TagWithCelestials>

     */

    /**
     * получить все api ключевые слова и небесные тела для заданного celestialId
     */
    /*
    @Transaction
    @Query("SELECT * FROM celestialinfoentity WHERE celestialId = :celestialId")
    fun getKeywordsByCelestialId(celestialId: Int): List<CelestialWithKeywords>

     */

    /**
     * получить все api ключевые слова и небесные тела для заданного nasaId
     */
    @Transaction
    @Query("SELECT * FROM celestialinfoentity WHERE nasa_id = :nasaId")
    fun getKeywordsByNasaId(nasaId: String): List<CelestialWithKeywords>



    @Query("SELECT * FROM keywordinfoentity WHERE name = :keywordName")
    fun getKeywordByName(keywordName: String): KeywordInfoEntity?

    @Query("SELECT dateFavouriteCreated FROM celestialinfoentity WHERE nasa_id = :nasaId")
    fun getFavouriteDate(nasaId: String): Long?

    // блок вставки
    /**
     * сохранить небесное тело
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCelestial(celestialInfoEntity: CelestialInfoEntity)

    /**
     * сохранить картинку небесного тела
     */
    @Query("UPDATE celestialinfoentity SET image = :image WHERE nasa_id = :nasaId")
    fun updateImage(nasaId: String, image: ByteArray)

    /**
     * сохранить дату сохранения небесного тела в избранных
     */
    @Query("UPDATE celestialinfoentity SET dateFavouriteCreated = :dateFavouriteCreated WHERE nasa_id = :nasaId")
    fun updateDateFavouriteCreated(nasaId: String, dateFavouriteCreated: Long?)

    /**
     * сохранить тег
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tagInfoEntity: TagInfoEntity)

    /**
     * сохранить привязку тега и небесного тела
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCelestialTagCrossRef(crossRef: CelestialTagCrossRef)

    /**
     * сохранить api ключевое слово
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKeyword(keywordInfoEntity: KeywordInfoEntity)

    /**
     * сохранить привязку api ключевого слова и небесного тела
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCelestialKeywordCrossRef(crossRef: CelestialKeywordCrossRef)

    // блок удаления

    /**
     * удалить небесное тело для заданного celestialId
     */
    /*
    @Query("DELETE FROM celestialinfoentity WHERE celestialId = :celestialId")
    fun deleteCelestial(celestialId: Int)

     */

    /**
     * удалить тег из БД
     */
    @Query("DELETE FROM taginfoentity WHERE tagId = :tagId")
    fun deleteTag(tagId: Int)

    /**
     * удалить привязку тега для заданного tagId
     */
    @Query("DELETE FROM celestialtagcrossref WHERE tagId = :tagId")
    fun deleteTagCrossRef(tagId:Int)


    /**
     * удалить привязку тега и небесного тела
     */
    @Query("DELETE FROM celestialtagcrossref WHERE celestialId = :celestialId AND tagId = :tagId")
    fun deleteCelestialTagsCrossRef(celestialId: Int, tagId:Int)

}