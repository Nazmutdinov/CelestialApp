package com.example.celestialapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// класс пользовательских тэгов
@Entity
data class TagInfoEntity(
    @PrimaryKey(autoGenerate = true) val tagId:Int = 0,
    val name: String
)
