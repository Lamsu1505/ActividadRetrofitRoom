package com.example.actividadretrofitroom.domain.repository

import androidx.paging.PagingData
import com.example.actividadretrofitroom.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun pagedCountries(query: String?, region: String?): Flow<PagingData<Country>>

    suspend fun refreshAll()
    suspend fun refreshByName(name: String)
    suspend fun refreshByRegion(region: String)

    fun observeCountry(cca3: String): Flow<Country?>
    suspend fun refreshDetail(cca3: String)
}

