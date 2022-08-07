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
    private val _keywords = MutableLiveData<List<TagDataItem>>()
    val keywords: LiveData<List<TagDataItem>> = _keywords

    private val _celestials = MutableLiveData<List<FavouriteCelestialDataItem>>()
    val celestials: LiveData<List<FavouriteCelestialDataItem>> = _celestials

    // для сообщениях об ошибках
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * получить список всех ключевых слов для списка nasaId небесных тел
     */
    fun getKeywords() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resource = getAllTagsUseCase()) {
                is ResourceUseCase.Success -> {
                    resource.data?.let { keywords ->
                        _keywords.postValue(keywords)

                        // получить список небеслых тел по фильтру ключевых слов
                        getCelestials(keywords)
                    }
                }
                else -> _errorMessage.postValue(resource.message)
            }
        }
    }

    /**
     * тапнули по тегу
     * нужно сменить состояние тега (включен\выключен)
     * заново получить список небесных тел для включенных тегов
     */
    fun tappedKeyword(keyword: TagDataItem) {
        // переключим тег
        keyword.toggle()

        _keywords.value?.map {
            it.copy()
        }?.map { tagDataItem ->
            if (tagDataItem.tagId == keyword.tagId) keyword else tagDataItem
        }?.let { newKeywordList ->
            viewModelScope.launch(Dispatchers.IO) {
                _keywords.postValue(newKeywordList)

                // обновим список небесных тел
                getCelestials(newKeywordList)
            }
        }
    }



    /**
     * получить список небесных тел для заданного списка ключевых слов
     */
    private suspend fun getCelestials(keywords: List<TagDataItem>) {
        // достанем список ключевых слов
        keywords.filter {
            // берем только включенные теги
            it.selected
        }.map { it.tagId }.let { filteredKeywordId ->
            when (val resource = getFavouriteCelestialsByTagUseCase(filteredKeywordId)) {
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

