package com.example.actividadretrofitroom.Data.Local.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.actividadretrofitroom.Data.Local.Entity.PaisEntity

@Dao
interface PaisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paises: List<PaisEntity>)

    @Query("SELECT * FROM paises")
    suspend fun getAll(): List<PaisEntity>

    @Query("SELECT * FROM paises WHERE region = :region")
    suspend fun getByRegion(region: String): List<PaisEntity>

    @Query("SELECT * FROM paises WHERE name LIKE '%' || :query || '%' OR cca3 LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<PaisEntity>

    @Query("DELETE FROM paises")
    suspend fun clearAll()

    @Transaction
    suspend fun updateAll(paises: List<PaisEntity>) {
        clearAll()
        insertAll(paises)
    }
}
