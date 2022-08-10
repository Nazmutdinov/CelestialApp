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
    override suspend fun saveCelestialDataIntoDB(
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
                // while celestial doesn't save into DB date will be empty
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


    override suspend fun updateDateFavouriteCelestialByNasaId(
        nasaId: String,
        dateFavouriteCreated: Long?
    ): Resource<CelestialInfoEntity> = withContext(dispatcher) {
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

    override suspend fun saveTagIntoDBAndGetTagId(tagName: String): Resource<Int> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val tagInfoEntity = TagInfoEntity(name = tagName)
                dao.insertTag(tagInfoEntity)
                dao.getTagIdByName(tagName)?.let { keywordId ->
                    return@withContext Resource.Success(keywordId)
                }

                return@withContext Resource.Error(context.getString(R.string.insertKeywordDataError))
            } catch (e: Exception) {
                return@withContext Resource.Error("${e.message}")
            }
        }

    override suspend fun saveBindingCelestialAndTagIntoDB(
        celestialId: Int,
        tagId: Int
    ): Resource<CelestialTagCrossRef> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val celestialTagCrossRef = CelestialTagCrossRef(celestialId, tagId)
            dao.insertCelestialTagCrossRef(celestialTagCrossRef)

            Resource.Success(celestialTagCrossRef)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun saveApiKeywordIntoDBAndGetId(keywordName: String): Resource<Int> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val keywordInfoEntity = KeywordInfoEntity(name = keywordName)
                dao.insertKeyword(keywordInfoEntity)
                val data = dao.getKeywordByName(keywordName)
                data?.let {
                    return@withContext Resource.Success(data.keywordId)
                }

                return@withContext Resource.Error(context.getString(R.string.emptyDataError))
            } catch (e: Exception) {
                return@withContext Resource.Error("${e.message}")
            }
        }

    override suspend fun saveBindingCelestialAndKeywordIntoDB(
        celestialId: Int,
        keywordId: Int
    ): Resource<CelestialKeywordCrossRef> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val celestialKeywordCrossRef = CelestialKeywordCrossRef(celestialId, keywordId)
            dao.insertCelestialKeywordCrossRef(celestialKeywordCrossRef)

            Resource.Success(celestialKeywordCrossRef)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun getCelestialsWithTagsByListNasaId(listNasaId: List<String>):
            Resource<List<CelestialWithTags>> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val data = dao.getCelestialWithTags(listNasaId)

            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun getTagsWithCelestialsByListTagId(listTagId: List<Int>):
            Resource<List<TagWithCelestials>> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            val data = dao.getTagsByListTagId(listTagId)

            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun getAllTags(): Resource<List<TagInfoEntity>> =
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

                return@withContext Resource.Error(
                    context.getString(
                        R.string.getCelestialError,
                        nasaId
                    )
                )
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getCelestialsWithKeywordsByNasaId(nasaId: String): Resource<List<CelestialWithKeywords>> =
        withContext(dispatcher) {
            return@withContext try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getKeywordsByNasaId(nasaId)

                return@withContext Resource.Success(data)
            } catch (e: Exception) {
                Resource.Error("${e.message}")
            }
        }

    override suspend fun getApiKeywordsByName(keywordName: String): Resource<KeywordInfoEntity> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getKeywordByName(keywordName)
                data?.let {
                    return@withContext Resource.Success(it)
                }

                return@withContext Resource.Error(context.getString(R.string.emptyDataError))
            } catch (e: Exception) {
                return@withContext Resource.Error("${e.message}")
            }
        }

    override suspend fun getDateAsLongFavouriteCelestialByNasaId(nasaId: String): Resource<Long> =
        withContext(dispatcher) {
            try {
                val dao = db.getConnectCelestialDao()
                val data = dao.getFavouriteDate(nasaId)
                data?.let {
                    return@withContext Resource.Success(it)
                }

                return@withContext Resource.Error(context.getString(R.string.emptyDataError))
            } catch (e: Exception) {
                return@withContext Resource.Error("${e.message}")
            }
        }

    override suspend fun deleteTagById(tagId: Int): Resource<Boolean> = withContext(dispatcher) {
        return@withContext try {
            val dao = db.getConnectCelestialDao()
            // delete binding tag and any celestials
            dao.deleteTagCrossRef(tagId)
            dao.deleteTag(tagId)

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }

    override suspend fun deleteBindingCelestialAndTag(
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

