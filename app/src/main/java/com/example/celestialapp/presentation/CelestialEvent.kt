package com.example.celestialapp.presentation

import com.example.celestialapp.R

sealed class CelestialEvent(val stringId: Int? = null) {
    class Add(stringId: Int = R.string.addKeywordMessage): CelestialEvent(stringId)
    class Save(stringId: Int = R.string.bindKeywordMessage): CelestialEvent(stringId)
    class Delete(stringId: Int = R.string.deleteKeywordMessage): CelestialEvent(stringId)
    class None: CelestialEvent()
}
