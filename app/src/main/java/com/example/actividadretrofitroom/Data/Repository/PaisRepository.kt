package com.example.actividadretrofitroom.Data.Repository

import com.example.actividadretrofitroom.Data.Local.Entity.PaisEntity
import com.example.actividadretrofitroom.Domain.CountryDetail
import com.example.actividadretrofitroom.Domain.CountryListItem

interface PaisRepository {
    // Local
    suspend fun insert(pais: PaisEntity)
    suspend fun getAllLocal(): List<PaisEntity>

    // Remote
    suspend fun getAllCountries(): Result<List<CountryListItem>>
    suspend fun getCountryByCode(code: String): Result<CountryDetail>
    suspend fun getCountriesByRegion(region: String): Result<List<CountryListItem>>
}
