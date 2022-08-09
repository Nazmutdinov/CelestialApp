package com.example.celestialapp.presentation.vm

import androidx.lifecycle.*
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.usecase.*
import com.example.celestialapp.presentation.CelestialEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Detailed view model for caching and viewing photos and desc of celestials NASA API
 */
@HiltViewModel
open class DetailedViewModel @Inject constructor(
    private val getFavouriteCelestialByIdUseCase: GetFavouriteCelestialByIdUseCase,
    private val getDetailedDataUseCase: GetDetailedDataUseCase,
    private val getTagsByNasaIdUseCase: GetTagsByNasaIdUseCase,
    private val getKeywordsByNasaIdUseCase: GetKeywordsByNasaIdUseCase,
    private val updateTagCelestialUseCase: UpdateTagCelestialUseCase,
    private val addTagCelestialUseCase: AddTagCelestialUseCase,
    private val deleteCrossRefDataUseCase: DeleteCrossRefDataUseCase
) : ViewModel() {
    // main data
    private val _detailedData = MutableLiveData<FavouriteCelestialDataItem>()
    val detailedData: LiveData<FavouriteCelestialDataItem> = _detailedData

    // events adding, updating or deleteing celestial's tags
    private val _eventCelestial = MutableLiveData<CelestialEvent>()
    val eventCelestial: LiveData<CelestialEvent> = _eventCelestial

    // our own tags
    private val _tags = MutableLiveData<List<TagDataItem>>()
    val tags: LiveData<List<TagDataItem>> = _tags

    // NASA keywords
    private val _keywords = MutableLiveData<List<String>?>()
    val keywords: LiveData<List<String>?> = _keywords

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadDataFromCacheOrAPI(nasaId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resourceCache = getFavouriteCelestialByIdUseCase(nasaId)

            if (resourceCache is ResourceUseCase.Success) {
                resourceCache.data?.let { _detailedData.postValue(it) }
            } else {
                loadDataFromNetwork(nasaId)
            }
        }
    }

    private suspend fun loadDataFromNetwork(nasaId: String) {
        when (val resource = getDetailedDataUseCase(nasaId)) {
            is ResourceUseCase.Success -> {
                resource.data?.let { dataItem ->
                    _detailedData.postValue(dataItem)
                 //   loadImageDataFromNetwork(dataItem.nasaId, dataItem.imagePath)
                }
            }
            else -> _errorMessage.postValue(resource.message)
        }
    }

    fun loadTags() {
        _detailedData.value?.let { favouriteCelestialDataItem ->
            val listNasaId = listOf(favouriteCelestialDataItem.nasaId)

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
     * add new tag, celestial and relation ship into db
     */
    fun addFavouriteCelestial(tagName: String) {
        _detailedData.value?.let { favouriteCelestial ->
            viewModelScope.launch(Dispatchers.IO) {
                // сохраним небесное тело в БД
                when (val resource =
                    addTagCelestialUseCase(tagName, favouriteCelestial)) {
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
     * update celestial with new tag
     */
    private fun saveFavouriteCelestial(tagId: Int) {
        _detailedData.value?.let { favouriteCelestial ->
            viewModelScope.launch(Dispatchers.IO) {
                when (val resource = updateTagCelestialUseCase(
                    tagId,
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

    private fun deleteFavouriteCelestial(keywordId: Int) {
        _detailedData.value?.let { favouriteCelestial ->
            viewModelScope.launch(Dispatchers.IO) {
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
     * tap on tag, e need change tag icon backgorund, bind/unbind tag to celestial
     */
    fun tappedTag(tag: TagDataItem) {
        tag.toggle()

        if (tag.selected) saveFavouriteCelestial(tag.tagId)
        else deleteFavouriteCelestial(tag.tagId)
    }

//    private fun updateCache(nasaId: String, bitmap: Bitmap) {
//        viewModelScope.launch(Dispatchers.IO) {
//            when (val resource = updateCacheUseCase(nasaId, bitmap)) {
//                is ResourceUseCase.Success -> resource.data?.let { _detailedData.postValue(it) }
//                else -> _errorMessage.postValue(resource.message)
//            }
//        }
//    }

    /**
     * load NASA keywords for celestial
     */
    fun loadKeywords() {
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