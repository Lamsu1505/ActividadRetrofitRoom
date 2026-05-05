package com.example.actividadretrofitroom.Domain

data class CountryListItem(
    val cca3: String,
    val name: String,
    val capital: String?,
    val region: String,
    val subregion: String?,
    val population: Long,
    val flagUrl: String,
)