package com.example.celestialapp.presentation.vm

import androidx.lifecycle.*
import androidx.paging.*
import com.example.celestialapp.domain.models.CelestialDataItem
import com.example.celestialapp.data.repository.MyPager
import com.example.celestialapp.presentation.CelestialName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val myPager: MyPager
) : ViewModel() {
    // поток небесных тел из api
    lateinit var celestialsFlow: Flow<PagingData<CelestialDataItem>>

    /* ключевые слова для поиска из navigation view меню
       по умолчанию это moon
     */
    private val _keywords = MutableLiveData<List<String>>()

    init {
        initiateFlow()
        initiateKeywords()
    }

    /**
     * зададим начальный поток небесных тел, который зависит от ключевых слов
     */
    private fun initiateFlow() {
        celestialsFlow = _keywords.asFlow()
            .flatMapLatest { list ->
                val pager = myPager(list, 10)

                pager.flow
            }
            .cachedIn(viewModelScope)
    }

    /**
     * зададим начальный список ключевых слов. По умолчанию это первый элемент CelestialName
     */
    private fun initiateKeywords() {
        _keywords.value = listOf(CelestialName.values().first().toString())
    }

    /**
     * получить поток небесных тел, смена keywords поиска
     * по умолчанию keywords = moon
     * keywords задается или из пунктов меню в HomeFragment
     * или в detailed fragment из тегов небесного тела
     */
    fun saveKeywords(keywords: List<String>) {
        // сохраним выбранный пункт в navigation view меню
        _keywords.value = keywords
    }
}