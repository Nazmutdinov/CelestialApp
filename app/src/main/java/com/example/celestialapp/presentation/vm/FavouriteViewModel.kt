package com.example.celestialapp.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.usecase.GetAllTagsUseCase
import com.example.celestialapp.domain.usecase.GetFavouriteCelestialsByTagUseCase
import com.example.celestialapp.domain.usecase.ResourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor (
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getFavouriteCelestialsByTagUseCase: GetFavouriteCelestialsByTagUseCase
) : ViewModel() {
    private val _tags = MutableLiveData<List<TagDataItem>>()
    val tags: LiveData<List<TagDataItem>> = _tags

    private val _celestials = MutableLiveData<List<FavouriteCelestialDataItem>>()
    val celestials: LiveData<List<FavouriteCelestialDataItem>> = _celestials

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * load tags and celestials for these tags from db
     */
    fun loadTags() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resource = getAllTagsUseCase()) {
                is ResourceUseCase.Success -> {
                    resource.data?.let { tags ->
                        _tags.postValue(tags)
                        loadCelestialsForSelectedTags(tags)
                    }
                }
                else -> _errorMessage.postValue(resource.message)
            }
        }
    }

    /**
     * when tag was tapped change tag state and refresh list celestials
     */
    fun tappedTag(tag: TagDataItem) {
        tag.toggle()

        _tags.value?.map {
            it.copy()
        }?.map { tagDataItem ->
            if (tagDataItem.tagId == tag.tagId) tag else tagDataItem
        }?.let { tagList ->
            viewModelScope.launch(Dispatchers.IO) {
                _tags.postValue(tagList)

                loadCelestialsForSelectedTags(tagList)
            }
        }
    }


    private suspend fun loadCelestialsForSelectedTags(tags: List<TagDataItem>) {
        tags.filter {
            it.selected
        }.map { it.tagId }.let { filteredTagId ->
            when (val resource = getFavouriteCelestialsByTagUseCase(filteredTagId)) {
                is ResourceUseCase.Success -> {
                    resource.data?.let {
                        _celestials.postValue(it)
                    }
                }
                else -> _errorMessage.postValue(resource.message)
            }
        }
    }
}

