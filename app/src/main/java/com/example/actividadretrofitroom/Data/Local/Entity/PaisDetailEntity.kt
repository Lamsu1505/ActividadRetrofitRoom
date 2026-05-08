package com.example.actividadretrofitroom.Data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.actividadretrofitroom.Domain.CountryDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "paises_detalles")
data class PaisDetailEntity(
    @PrimaryKey val cca3: String,
    val name: String,
    val officialName: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val languages: String, // Comma separated
    val currencies: String, // JSON String
    val population: Long,
    val area: Double,
    val flagUrl: String,
    val borders: String, // Comma separated
    val continents: String // Comma separated
)

fun PaisDetailEntity.toDomain(): CountryDetail {
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, String>>() {}.type
    val currenciesMap: Map<String, String> = gson.fromJson(currencies, mapType)

    return CountryDetail(
        name = name,
        officialName = officialName,
        capital = capital,
        region = region,
        subregion = subregion,
        languages = if (languages.isEmpty()) emptyList() else languages.split("|"),
        currencies = currenciesMap,
        population = population,
        area = area,
        flagUrl = flagUrl,
        cca3 = cca3,
        borders = if (borders.isEmpty()) emptyList() else borders.split("|"),
        continents = if (continents.isEmpty()) emptyList() else continents.split("|")
    )
}

fun CountryDetail.toEntity(): PaisDetailEntity {
    val gson = Gson()
    return PaisDetailEntity(
        cca3 = cca3,
        name = name,
        officialName = officialName,
        capital = capital,
        region = region,
        subregion = subregion,
        languages = languages.joinToString("|"),
        currencies = gson.toJson(currencies),
        population = population,
        area = area,
        flagUrl = flagUrl,
        borders = borders.joinToString("|"),
        continents = continents.joinToString("|")
    )
}
