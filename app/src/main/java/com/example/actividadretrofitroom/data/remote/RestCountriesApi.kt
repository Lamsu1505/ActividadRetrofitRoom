package com.example.actividadretrofitroom.data.remote

import com.example.actividadretrofitroom.data.remote.dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestCountriesApi {
    @GET("v3.1/all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = DEFAULT_FIELDS,
    ): List<CountryDto>

    @GET("v3.1/name/{name}")
    suspend fun searchByName(
        @Path("name") name: String,
        @Query("fields") fields: String = DEFAULT_FIELDS,
    ): List<CountryDto>

    @GET("v3.1/region/{region}")
    suspend fun filterByRegion(
        @Path("region") region: String,
        @Query("fields") fields: String = DEFAULT_FIELDS,
    ): List<CountryDto>

    @GET("v3.1/alpha/{code}")
    suspend fun getByAlpha(
        @Path("code") code: String,
        @Query("fields") fields: String = DETAIL_FIELDS,
    ): List<CountryDto>

    companion object {
        // Base for list/search screens.
        const val DEFAULT_FIELDS =
            "cca3,name,flags,region,subregion,capital,population"

        // Base for detail screen.
        const val DETAIL_FIELDS =
            "cca3,name,flags,region,subregion,capital,population,area,languages,currencies,timezones,latlng"
    }
}

