package com.example.actividadretrofitroom.Data.Remote

import com.example.actividadretrofitroom.Data.Remote.Dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PaisApiService {

    // Lista de todos los países
    @GET("all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = "name,flags,capital,region,subregion,population,cca3,area,languages"
    ): List<CountryDto>

    // Buscar por nombre
    @GET("name/{name}")
    suspend fun searchByName(
        @Path("name") name: String
    ): List<CountryDto>

    // Filtrar por región
    @GET("region/{region}")
    suspend fun getByRegion(
        @Path("region") region: String
    ): List<CountryDto>

    // Detalle por código
    @GET("alpha/{code}")
    suspend fun getByCode(
        @Path("code") code: String
    ): List<CountryDto>


}