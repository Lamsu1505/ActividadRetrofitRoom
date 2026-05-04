package com.example.actividadretrofitroom.data.mapper

import com.example.actividadretrofitroom.data.local.entity.CountryEntity
import com.example.actividadretrofitroom.data.remote.dto.CountryDto
import com.example.actividadretrofitroom.domain.model.Country
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

private fun moshiStringMapAdapter(moshi: Moshi) =
    moshi.adapter<Map<String, String>>(
        Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    )

private fun moshiAnyMapAdapter(moshi: Moshi) =
    moshi.adapter<Map<String, Any?>>(
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    )

fun CountryDto.toEntity(nowEpochMs: Long, moshi: Moshi): CountryEntity? {
    val code = cca3?.trim().orEmpty()
    val common = name?.common?.trim().orEmpty()
    if (code.isBlank() || common.isBlank()) return null

    val languagesJson = languages?.let { moshiStringMapAdapter(moshi).toJson(it) }

    val currenciesDisplay: Map<String, String>? = currencies?.mapValues { (_, c) ->
        listOfNotNull(c.name?.trim(), c.symbol?.trim()).filter { it.isNotBlank() }.joinToString(" ")
    }?.filterValues { it.isNotBlank() }
    val currenciesJson = currenciesDisplay?.let { moshiStringMapAdapter(moshi).toJson(it) }

    val lat = latlng?.getOrNull(0)
    val lng = latlng?.getOrNull(1)

    return CountryEntity(
        cca3 = code,
        nameCommon = common,
        nameOfficial = name?.official?.trim()?.takeIf { it.isNotBlank() },
        flagPng = flags?.png,
        flagAlt = flags?.alt,
        region = region?.trim()?.takeIf { it.isNotBlank() },
        subregion = subregion?.trim()?.takeIf { it.isNotBlank() },
        capitalCsv = capital?.filterNotNull()?.joinToString(",")?.takeIf { it.isNotBlank() },
        population = population,
        area = area,
        languagesJson = languagesJson,
        currenciesJson = currenciesJson,
        timezonesCsv = timezones?.filterNotNull()?.joinToString(",")?.takeIf { it.isNotBlank() },
        lat = lat,
        lng = lng,
        lastUpdatedEpochMs = nowEpochMs,
    )
}

fun CountryEntity.toDomain(moshi: Moshi): Country {
    val mapAdapter = moshiStringMapAdapter(moshi)
    val languages = languagesJson?.let { runCatching { mapAdapter.fromJson(it) }.getOrNull() }.orEmpty()
    val currencies = currenciesJson?.let { runCatching { mapAdapter.fromJson(it) }.getOrNull() }.orEmpty()

    val latLng = if (lat != null && lng != null) (lat to lng) else null

    return Country(
        cca3 = cca3,
        nameCommon = nameCommon,
        nameOfficial = nameOfficial,
        flagPng = flagPng,
        flagAlt = flagAlt,
        region = region,
        subregion = subregion,
        capitals = capitalCsv?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }.orEmpty(),
        population = population,
        area = area,
        languages = languages,
        currencies = currencies,
        timezones = timezonesCsv?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }.orEmpty(),
        latLng = latLng,
    )
}

