package com.example.actividadretrofitroom.data.remote.dto

import com.squareup.moshi.Json

data class CountryDto(
    @Json(name = "cca3") val cca3: String?,
    @Json(name = "name") val name: NameDto?,
    @Json(name = "flags") val flags: FlagsDto?,
    @Json(name = "region") val region: String?,
    @Json(name = "subregion") val subregion: String?,
    @Json(name = "capital") val capital: List<String>?,
    @Json(name = "population") val population: Long?,
    @Json(name = "area") val area: Double?,
    @Json(name = "languages") val languages: Map<String, String>?,
    @Json(name = "currencies") val currencies: Map<String, CurrencyDto>?,
    @Json(name = "timezones") val timezones: List<String>?,
    @Json(name = "latlng") val latlng: List<Double>?,
)

data class NameDto(
    @Json(name = "common") val common: String?,
    @Json(name = "official") val official: String?,
)

data class FlagsDto(
    @Json(name = "png") val png: String?,
    @Json(name = "svg") val svg: String?,
    @Json(name = "alt") val alt: String?,
)

data class CurrencyDto(
    @Json(name = "name") val name: String?,
    @Json(name = "symbol") val symbol: String?,
)

