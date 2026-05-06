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
        cca3 = cca3 ?: "UNK", // Si no hay código, ponemos "Unknown"
        name = name?.common ?: "Sin nombre",
        capital = capital?.firstOrNull() ?: "N/A",
        region = region ?: "Desconocida",
        subregion = "N/A",
        population = population ?: 0L,
        flagUrl = flags?.png ?: ""
    )
}

//fun CountryDto.toDomainDetail(): CountryDetail {
//    return CountryDetail(
//        name = name.common,
//        officialName = name.official,
//        capital = capital?.firstOrNull() ?: "N/A",
//        region = region,
//        subregion = subregion ?: "N/A",
//        languages = languages?.values?.toList() ?: emptyList(),
//        currencies = currencies?.mapValues { "${it.value.name} (${it.value.symbol ?: ""})" } ?: emptyMap(),
//        population = population,
//        area = area ?: 0.0,
//        flagUrl = flags.png,
//        cca3 = cca3,
//        borders = borders ?: emptyList(),
//        continents = continents ?: emptyList()
//    )
//}
