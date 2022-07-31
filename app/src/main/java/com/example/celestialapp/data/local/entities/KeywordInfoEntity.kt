package com.example.celestialapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// класс api тегов
@Entity
data class KeywordInfoEntity(
    @PrimaryKey(autoGenerate = true) val keywordId:Int = 0,
    val name: String
)
