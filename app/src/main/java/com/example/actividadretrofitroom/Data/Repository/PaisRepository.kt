package com.example.actividadretrofitroom.Data.Repository

import com.example.actividadretrofitroom.Domain.CountryDetail
import com.example.actividadretrofitroom.Domain.CountryListItem

interface PaisRepository {
    // Remote + Local logic
    suspend fun getAllCountries(): Result<List<CountryListItem>>
    suspend fun getCountryByCode(code: String): Result<CountryDetail>
    suspend fun getCountriesByRegion(region: String): Result<List<CountryListItem>>
    suspend fun searchCountries(query: String): Result<List<CountryListItem>>
}
