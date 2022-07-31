package com.example.celestialapp.data.repository

import android.content.Context
import com.example.celestialapp.R
import com.example.celestialapp.data.local.CelestialDatabase
import com.example.celestialapp.data.local.entities.*
import com.example.celestialapp.data.local.relations.CelestialWithKeywords
import com.example.celestialapp.data.local.relations.CelestialWithTags
import com.example.celestialapp.data.local.relations.TagWithCelestials
import com.example.celestialapp.domain.repository.LocalDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class LocalDataRepositoryImpl(
    private val context: Context,
    private val db: CelestialDatabase,
    private val dispatcher: CoroutineDispatcher
) : LocalDataRepository {
    /**
     * сохранить небесное тело в БД
     */
    override suspend fun insertCelestialData(
        nasaId: String,
        title: String,
        date: String,
        description: String,
        image: ByteArray?,
        imagePath: String
    ): Resource<CelestialInfoEntity> = withContext(dispatcher) {
        try {
            val dao = db.getConnectCelestialDao()
            val celestialInfoEntity = CelestialInfoEntity(
                nasaId = nasaId,
                title = title,
                date = date,
                description = description,
                image = image,
                imagePath = imagePath,
                // пока небесное тело не сохранено в избранные, дата будет пустой
                dateFavouriteCreated = null
            )

            dao.insertCelestial(celestialInfoEntity)

            dao.getCelestialByNasaId(nasaId)?.let {
                return@withContext Resource.Success(it)
            }

            return@withContext Resource.Error(context.getString(R.string.insertCelestialDataError))

        } catch (e: Exception) {
            return@withContext Resource.Error("${e.message}")
        }
    }

    override suspend fun updateImageData(
        nasaId: String,
        image: ByteArray
    ): Resource<CelestialInfoEntity> = withContext(dispatcher) {
        try {
            val dao = db.getConnectCelestialDao()
            dao.updateImage(nasaId, image)

            dao.getCelestialByNasaId(nasaId)?.let {
                return@withContext Resource.Success(it)
            }

            return@withContext Resource.Error(context.getString(R.string.insertCelestialDataError))

        } catch (e: Exception) {
            return@withContext Resource.Error("${e.message}")
        }
    }

    override suspend fun updateFavouriteDate(
        nasaId: String,
        dateFavouriteCreated: Long?
    ): Resource<CelestialInfoEntity>  = withContext(dispatcher) {
        try {
            val dao = db.getConnectCelestialDao()
            dao.updateDateFavouriteCreated(nasaId, dateFavouriteCreated)

            dao.getCelestialByNasaId(nasaId)?.let {
                return@withContext Resource.Success(it)
            }

            return@withContext Resource.Error(context.getString(R.string.insertCelestialDataError))

        } catch (e: Exception) {
            return@withContext Resource.Error("${e.message}")
        }
    }

    /**
     * сохранить данные по ключевому слову в БД, получить keywordId
     */
    override suspend fun insertTagData(tagName: String): Resource<Int> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val tagInfoEntity = TagInfoEntity(name = tagName)

                // вставка в БД
                dao.insertTag(tagInfoEntity)

                // получение id из БД
                dao.getTagIdByName(tagName)?.let { keywordId ->
                    return@withContext Resource.Success(keywordId)
                }
                return@withContext Resource.Error(context.getString(R.string.insertKeywordDataError))

            } catch (e: Exception) {
                return@withContext Resource.Error("${e.message}")
            }
        }

    override suspend fun insertCelestialTagsCrossRef(
        celestialId: Int,
        tagId: Int
    ): Resource<CelestialTagCrossRef> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val celestialTagCrossRef = CelestialTagCrossRef(
                celestialId,
                tagId
            )

            dao.insertCelestialTagCrossRef(celestialTagCrossRef)

            Resource.Success(celestialTagCrossRef)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun insertKeywordData(keywordName: String): Resource<Int> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val keywordInfoEntity = KeywordInfoEntity(name = keywordName)

                dao.insertKeyword(keywordInfoEntity)

                val data = dao.getKeywordByName(keywordName)

                data?.let {
                    return@withContext Resource.Success(data.keywordId)
                }

                return@withContext  Resource.Error("пусто")
            } catch (e: Exception) {
              return@withContext  Resource.Error("${e.message}")
            }
        }

    override suspend fun insertCelestialKeywordsCrossRef(
        celestialId: Int,
        keywordId: Int
    ): Resource<CelestialKeywordCrossRef> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val celestialKeywordCrossRef = CelestialKeywordCrossRef(
                celestialId, keywordId
            )

            dao.insertCelestialKeywordCrossRef(celestialKeywordCrossRef)

            Resource.Success(celestialKeywordCrossRef)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    /**
     * достать все ключи и небесные тела из БД для заданного списка nasaId
     */
    override suspend fun getDataByListNasaId(listNasaId: List<String>): Resource<List<CelestialWithTags>> =
        withContext(dispatcher)
        {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getCelestialWithTags(listNasaId)

                Resource.Success(data)
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    /**
     * достать все небесные тела и ключи для заданного списка ключевых слов
     */
    override suspend fun getDataByListTagId(listTagId: List<Int>):
            Resource<List<TagWithCelestials>> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val data = dao.getTagsByListTagId(listTagId)

            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun getTags(): Resource<List<TagInfoEntity>> =
        withContext(dispatcher) {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getTags()

                Resource.Success(data)
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getExclusiveTags(listTagId: List<Int>): Resource<List<TagInfoEntity>> =
        withContext(dispatcher) {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getExclusiveTags(listTagId)

                Resource.Success(data)
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getTagIdByName(tagName: String): Resource<Int> =
        withContext(dispatcher) {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getTagIdByName(tagName)
                data?.let {
                    return@withContext Resource.Success(it)
                }

                Resource.Error(context.getString(R.string.getKeywordIdError, tagName))
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getCelestialByNasaId(nasaId: String): Resource<CelestialInfoEntity> =
        withContext(dispatcher) {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getCelestialByNasaId(nasaId)

                data?.let {
                    return@withContext Resource.Success(it)
                }

                val message = context.getString(R.string.getCelestialError, nasaId)
                return@withContext Resource.Error(message)
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getKeywordsByNasaId(nasaId: String): Resource<List<CelestialWithKeywords>> =
        withContext(dispatcher) {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getKeywordsByNasaId(nasaId)

                return@withContext Resource.Success(data)
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getKeywordsByName(keywordName: String): Resource<KeywordInfoEntity> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getKeywordByName(keywordName)

                data?.let {
                    return@withContext Resource.Success(it)
                }

                return@withContext  Resource.Error("пусто")
            } catch (e: Exception) {
              return@withContext  Resource.Error("${e.message}")
            }
        }

    override suspend fun getFavouriteDate(nasaId: String): Resource<Long> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getFavouriteDate(nasaId)

                data?.let {
                    return@withContext Resource.Success(it)
                }

                return@withContext  Resource.Error("пусто")
            } catch (e: Exception) {
                return@withContext  Resource.Error("${e.message}")
            }
        }

    /**
     * удалить ключевое слово
     */
    override suspend fun deleteTagData(tagId: Int): Resource<Boolean> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            // удалим связь ключевого слова и любых небесных тел
            dao.deleteTagCrossRef(tagId)

            // удалим ключевое слово
            dao.deleteTag(tagId)

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    /**
     * удалить связь ключевого слова и небесного тела в БД
     */
    override suspend fun deleteCelestialTagsCrossRef(
        celestialId: Int,
        tagId: Int
    ): Resource<Boolean> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            dao.deleteCelestialTagsCrossRef(celestialId, tagId)

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }
}