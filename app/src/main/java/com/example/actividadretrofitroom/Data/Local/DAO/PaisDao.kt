package com.example.actividadretrofitroom.Data.Local.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.actividadretrofitroom.Data.Local.Entity.PaisEntity

@Dao
interface PaisDao {
    @Insert
    suspend fun insert(pais: PaisEntity)

    @Query("SELECT * FROM pais")
    suspend fun getAll(): List<PaisEntity>
}