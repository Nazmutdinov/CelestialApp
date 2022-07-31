package com.example.celestialapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.celestialapp.data.Constants
import com.example.celestialapp.data.local.entities.*

@Database(
    entities = [
        CelestialInfoEntity::class,
        TagInfoEntity::class,
        KeywordInfoEntity::class,
        CelestialTagCrossRef::class,
        CelestialKeywordCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CelestialDatabase : RoomDatabase() {
    abstract fun getConnectCelestialDao(): CelestialInfoDao

    companion object {
        @Volatile
        private var INSTANCE: CelestialDatabase? = null

        fun getDatabase(context: Context): CelestialDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    CelestialDatabase::class.java,
                    Constants.database
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}