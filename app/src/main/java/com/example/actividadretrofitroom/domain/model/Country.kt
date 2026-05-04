package com.example.actividadretrofitroom.domain.model

data class Country(
    val cca3: String,
    val nameCommon: String,
    val nameOfficial: String?,
    val flagPng: String?,
    val flagAlt: String?,
    val region: String?,
    val subregion: String?,
    val capitals: List<String>,
    val population: Long?,
    val area: Double?,
    val languages: Map<String, String>,
    val currencies: Map<String, String>,
    val timezones: List<String>,
    val latLng: Pair<Double, Double>?,
)

