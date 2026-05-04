package com.example.actividadretrofitroom.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.actividadretrofitroom.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Upsert
    suspend fun upsertAll(items: List<CountryEntity>)

    @Query("SELECT * FROM countries ORDER BY nameCommon ASC")
    fun pagingAll(): PagingSource<Int, CountryEntity>

    @Query(
        """
        SELECT * FROM countries
        WHERE (:query IS NULL OR nameCommon LIKE '%' || :query || '%')
          AND (:region IS NULL OR region = :region)
        ORDER BY nameCommon ASC
        """
    )
    fun pagingFiltered(query: String?, region: String?): PagingSource<Int, CountryEntity>

    @Query("SELECT * FROM countries WHERE cca3 = :cca3 LIMIT 1")
    fun observeByCca3(cca3: String): Flow<CountryEntity?>
}

