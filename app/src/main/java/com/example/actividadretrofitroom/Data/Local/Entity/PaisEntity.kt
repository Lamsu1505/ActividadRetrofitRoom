package com.example.actividadretrofitroom.Data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.actividadretrofitroom.Domain.CountryListItem

@Entity(tableName = "paises")
data class PaisEntity(
    @PrimaryKey val cca3: String,
    val name: String,
    val capital: String?,
    val region: String,
    val subregion: String?,
    val population: Long,
    val area: Double,
    val languages: String, // Guardado como String separado por comas
    val flagUrl: String
)

fun PaisEntity.toDomain(): CountryListItem {
    return CountryListItem(
        cca3 = cca3,
        name = name,
        capital = capital,
        region = region,
        subregion = subregion,
        population = population,
        area = area,
        languages = if (languages.isEmpty()) emptyList() else languages.split(", "),
        flagUrl = flagUrl
    )
}

fun CountryListItem.toEntity(): PaisEntity {
    return PaisEntity(
        cca3 = cca3,
        name = name,
        capital = capital,
        region = region,
        subregion = subregion,
        population = population,
        area = area,
        languages = languages.joinToString(", "),
        flagUrl = flagUrl
    )
}
