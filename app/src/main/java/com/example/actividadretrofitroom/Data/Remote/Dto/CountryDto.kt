package com.example.actividadretrofitroom.Data.Remote.Dto

import com.example.actividadretrofitroom.Domain.CountryDetail
import com.example.actividadretrofitroom.Domain.CountryListItem
import com.google.gson.annotations.SerializedName

data class CountryDto(
    val name: NameDto?,
    val cca3: String?,
    val flags: FlagsDto?,
    val capital: List<String>?,
    val region: String?,
    val subregion: String?,
    val population: Long?,
    val area: Double?,
    val languages: Map<String, String>?,
    val currencies: Map<String, CurrencyDto>?,
    val borders: List<String>?,
    val continents: List<String>?
)

data class NameDto(
    val common: String,
    val official: String
)

data class FlagsDto(
    val png: String,
    val svg: String
)

data class CurrencyDto(
    val name: String,
    val symbol: String?
)

fun CountryDto.toDomainListItem(): CountryListItem {
    return CountryListItem(
        cca3 = cca3 ?: "UNK",
        name = name?.common ?: "Sin nombre",
        capital = capital?.firstOrNull() ?: "N/A",
        region = region ?: "Desconocida",
        subregion = subregion ?: "N/A",
        population = population ?: 0L,
        area = area ?: 0.0,
        languages = languages?.values?.toList() ?: emptyList(),
        flagUrl = flags?.png ?: ""
    )
}

fun CountryDto.toDomainDetail(): CountryDetail {
    return CountryDetail(
        cca3 = cca3 ?: "UNK",
        name = name?.common ?: "Sin nombre",
        officialName = name?.official ?: "Sin nombre oficial",
        capital = capital?.firstOrNull() ?: "N/A",
        region = region ?: "Desconocida",
        subregion = subregion ?: "Desconocida",
        population = population ?: 0L,
        area = area ?: 0.0,
        languages = languages?.values?.toList() ?: emptyList(),
        currencies = currencies?.mapValues { it.value.name } ?: emptyMap(),
        borders = borders ?: emptyList(),
        continents = continents ?: emptyList(),
        flagUrl = flags?.png ?: ""
    )
}
