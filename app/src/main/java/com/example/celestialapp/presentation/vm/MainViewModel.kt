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
    var celestialsFlowFromAPI: Flow<PagingData<CelestialDataItem>>? = null

    // by default it's MOON
    private val _menuItemNames = MutableLiveData<List<String>>()

    init {
        initiateFlowCelestialsFromAPI()
        setupDefaultKeywordForAPI()
    }

    private fun initiateFlowCelestialsFromAPI() {
        celestialsFlowFromAPI = _menuItemNames.asFlow()
            .flatMapLatest { list ->
                val pager = myPager(list, 10)
                pager.flow
            }
            .cachedIn(viewModelScope)
    }

    private fun setupDefaultKeywordForAPI() {
        // by default first element - it's MOON
        val defaultKeyword = CelestialName.values().first()
        _menuItemNames.value = listOf(defaultKeyword.toString())
    }

    /**
     * by default keywords = moon
     * keywords define by either HomeFragment navigation menu item
     * or by NASA API keywords of celestials (see DetailsFragment)
     */
    fun saveSelectedMenuItemName(menuItemName: String) {
        _menuItemNames.value = listOf(menuItemName)
    }

    fun saveApiKeywords(keywordsApi: List<String>) {
        _menuItemNames.value = keywordsApi
    }
}