package com.example.actividadretrofitroom.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.actividadretrofitroom.data.local.entity.CountryEntity

@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
}

