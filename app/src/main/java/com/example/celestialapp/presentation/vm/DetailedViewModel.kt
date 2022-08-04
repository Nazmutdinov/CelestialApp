package com.example.celestialapp.presentation.vm

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.usecase.*
import com.example.celestialapp.presentation.CelestialEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// модель для кэширования и просмотра детальной инфы по небесным телам из api
@HiltViewModel
open class DetailedViewModel @Inject constructor (
    private val getFavouriteCelestialByIdUseCase: GetFavouriteCelestialByIdUseCase,
    private val getDetailedDataUseCase: GetDetailedDataUseCase,
    private val getTagsByNasaIdUseCase: GetTagsByNasaIdUseCase,
    private val getKeywordsByNasaIdUseCase: GetKeywordsByNasaIdUseCase,
    private val updateTagCelestialUseCase: UpdateTagCelestialUseCase,
    private val addTagCelestialUseCase: AddTagCelestialUseCase,
    private val deleteCrossRefDataUseCase: DeleteCrossRefDataUseCase,
    private val updateCacheUseCase: UpdateCacheUseCase
) : ViewModel() {
    // детальная инфа о небесном теле
    private val _detailedData = MutableLiveData<FavouriteCelestialDataItem>()
    val detailedData: LiveData<FavouriteCelestialDataItem> = _detailedData

    // события добавления, обновления и удаления тега
    private val _eventCelestial = MutableLiveData<CelestialEvent>()
    val eventCelestial: LiveData<CelestialEvent> = _eventCelestial

    // список тегов
    private val _tags = MutableLiveData<List<TagDataItem>>()
    val tags: LiveData<List<TagDataItem>> = _tags

    // список api ключевых слов
    private val _keywords = MutableLiveData<List<String>?>()
    val keywords: LiveData<List<String>?> = _keywords

    // для сообщениях об ошибках
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadDataFromCacheAndAPI(nasaIdNullable: String?) {
        nasaIdNullable?.let { nasaId ->
            viewModelScope.launch(Dispatchers.IO) {
                // попробуем получить данные из кэша
                val resourceCache = getFavouriteCelestialByIdUseCase(nasaId)

                if (resourceCache is ResourceUseCase.Success) {
                    resourceCache.data?.let { favouriteCelestialDataItem ->
                        // передадим подписанту
                        _detailedData.postValue(favouriteCelestialDataItem)
                    }
                } else {
                    // данных в кэше не найдено, поэтому берем данные из сети
                    when (val resource = getDetailedDataUseCase(nasaId)) {
                        is ResourceUseCase.Success -> {
                            resource.data?.let { dataItem ->
                                // передадим подписанту
                                _detailedData.postValue(dataItem)
                            }
                        }
                        else -> _errorMessage.postValue(resource.message)
                    }
                }
            }
        }
    }

    /**
     * получить список привязок тегов для списка nasaId небесных тел
     */
    fun getTags() {
        _detailedData.value?.let {
            val listNasaId = listOf(it.nasaId)

            viewModelScope.launch(Dispatchers.IO) {
                when (val resource = getTagsByNasaIdUseCase(listNasaId)) {
                    is ResourceUseCase.Success -> {
                        resource.data?.let { listKeyword ->
                            _tags.postValue(listKeyword)
                        }
                    }
                    else -> _errorMessage.postValue(resource.message)
                }
            }
        }
    }

    /**
     * добавить тег, небесное тело и связь в БД
     */
    fun addFavouriteCelestial(keywordName: String) {
        _detailedData.value?.let { favouriteCelestial ->
            viewModelScope.launch(Dispatchers.IO) {
                // сохраним небесное тело в БД
                when (val resource =
                    addTagCelestialUseCase(keywordName, favouriteCelestial)) {
                    is ResourceUseCase.Success -> {
                        resource.data?.let {
                            _eventCelestial.postValue(CelestialEvent.Add())
                        }
                    }
                    else -> _errorMessage.postValue(resource.message)
                }
            }
        }

    }

    /**
     * сохраним существующий тег, небесное тело и связь в БД
     */
    private fun saveFavouriteCelestial(keywordId: Int) {
        _detailedData.value?.let { favouriteCelestial ->
            viewModelScope.launch(Dispatchers.IO) {
                // сохраним небесное тело в БД
                when (val resource = updateTagCelestialUseCase(
                    keywordId,
                    favouriteCelestial
                )) {
                    is ResourceUseCase.Success -> {
                        resource.data?.let {
                            _eventCelestial.postValue(CelestialEvent.Save())
                        }
                    }
                    else -> _errorMessage.postValue(resource.message)
                }
            }
        }
    }

    /**
     * удалим связь тега и небесного тела
     */
    private fun deleteFavouriteCelestial(keywordId: Int) {
        _detailedData.value?.let { favouriteCelestial ->
            viewModelScope.launch(Dispatchers.IO) {
                // удалим тег для небесного тела
                when (val resource =
                    deleteCrossRefDataUseCase(keywordId, favouriteCelestial.celestialId)) {
                    is ResourceUseCase.Success -> {
                        resource.data?.let {
                            _eventCelestial.postValue(CelestialEvent.Delete())
                        }
                    }
                    else -> _errorMessage.postValue(resource.message)
                }
            }
        }
    }

    /**
     * тапнули по тэгу, нужно сменить картинку и привязать/отвязать тег к небесному телу
     */
    fun tappedKeyword(keyword: TagDataItem) {
        // переключим тег
        keyword.toggle()

        // если выбрали ключевое слово, то сохраним привязку иначе удалим привязку
        if (keyword.selected) saveFavouriteCelestial(keyword.tagId) else deleteFavouriteCelestial(
            keyword.tagId
        )
    }

    /**
     * сохранить bitmap medium картинку в кэш БД
     */
    fun updateCache(nasaId: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("myTag","nasaId $nasaId $bitmap")
            val resource = updateCacheUseCase(nasaId, bitmap)

            // если ошибка, сообщим подписанту
            if (resource is ResourceUseCase.Error) _errorMessage.postValue(resource.message)
        }
    }

    /**
     * получить список привязок ключевых слов для небесного тела
     */
    fun getKeywords() {
        _detailedData.value?.let { favouriteCelestialDataItem ->
            val nasaId = favouriteCelestialDataItem.nasaId
            viewModelScope.launch(Dispatchers.IO) {
                when (val resource = getKeywordsByNasaIdUseCase(nasaId)) {
                    is ResourceUseCase.Success -> {
                        _keywords.postValue(resource.data)
                    }
                    else -> {
                        _errorMessage.postValue(resource.message)
                    }
                }
            }
        }

    }
}