package com.example.actividadretrofitroom.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val cca3: String,
    val nameCommon: String,
    val nameOfficial: String?,
    val flagPng: String?,
    val flagAlt: String?,
    val region: String?,
    val subregion: String?,
    val capitalCsv: String?,
    val population: Long?,
    val area: Double?,
    val languagesJson: String?,
    val currenciesJson: String?,
    val timezonesCsv: String?,
    val lat: Double?,
    val lng: Double?,
    val lastUpdatedEpochMs: Long,
)

