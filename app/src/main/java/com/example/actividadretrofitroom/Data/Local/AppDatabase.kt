package com.example.actividadretrofitroom.Data.Local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.actividadretrofitroom.Data.Local.DAO.PaisDao
import com.example.actividadretrofitroom.Data.Local.Entity.PaisEntity


@Database(entities = [PaisEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paisDao(): PaisDao
}