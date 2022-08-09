package com.example.celestialapp.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.celestialapp.domain.usecase.GetFavouriteCelestialByIdUseCase
import com.example.celestialapp.domain.usecase.ResourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZoomViewModel @Inject constructor (
    private val getFavouriteCelestialByIdUseCase: GetFavouriteCelestialByIdUseCase
) : ViewModel() {

    private val _image = MutableLiveData<ByteArray?>()
    val image: LiveData<ByteArray?> = _image

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * load image from local db
     */
    fun loadImage(id: String?) {
        id?.let { nasaId ->
            viewModelScope.launch(Dispatchers.IO) {
                when (val resource = getFavouriteCelestialByIdUseCase(nasaId)) {
                    is ResourceUseCase.Success -> {
                        resource.data?.let { favouriteCelestialDataItem ->
                            _image.postValue(favouriteCelestialDataItem.image)
                        }
                    }
                    else -> _errorMessage.postValue(resource.message)
                }
            }
        }
    }
}

