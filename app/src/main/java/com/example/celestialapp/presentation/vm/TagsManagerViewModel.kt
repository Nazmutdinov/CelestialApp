package com.example.celestialapp.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.domain.usecase.DeleteTagsDataUseCase
import com.example.celestialapp.domain.usecase.GetAllTagsUseCase
import com.example.celestialapp.domain.usecase.InsertTagDataUseCase
import com.example.celestialapp.domain.usecase.ResourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeywordsManagerViewModel @Inject constructor (
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val insertTagDataUseCase: InsertTagDataUseCase,
    private val deleteTagsDataUseCase: DeleteTagsDataUseCase
) : ViewModel() {
    private val _tags = MutableLiveData<List<TagDataItem>>()
    val tags: LiveData<List<TagDataItem>> = _tags

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadTags() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resource = getAllTagsUseCase()) {
                is ResourceUseCase.Success -> {
                    resource.data?.let {
                        _tags.postValue(it)
                    }
                }
                else -> _errorMessage.postValue(resource.message)
            }
        }
    }

    fun addTag (newTagName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resource = insertTagDataUseCase(newTagName)
            if (resource is ResourceUseCase.Error) _errorMessage.postValue(resource.message)
        }
    }

    /**
     * удалить тег
     */
    fun deleteKeyword (keywordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resource = deleteTagsDataUseCase(keywordId)) {
                // запустим обновление спика тегов у подписанта
                is ResourceUseCase.Success -> loadTags()
                else -> _errorMessage.postValue(resource.message)
            }
        }
    }

    /**
     * получить ключевое слово по индексу массива
      */
    fun getKeywordByPosition(position: Int): TagDataItem? = _tags.value?.get(position)

}

