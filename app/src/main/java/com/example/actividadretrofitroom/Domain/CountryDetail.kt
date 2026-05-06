package com.example.actividadretrofitroom.Domain

data class CountryDetail(
    val name: String,
    val officialName: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val languages: List<String>,
    val currencies: Map<String, String>, // Código -> Nombre
    val population: Long,
    val area: Double,
    val flagUrl: String,
    val cca3: String,
    val borders: List<String>,
    val continents: List<String>
)