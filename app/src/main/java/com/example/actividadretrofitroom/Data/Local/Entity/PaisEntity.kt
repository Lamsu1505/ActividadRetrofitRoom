package com.example.actividadretrofitroom.Data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pais")
data class PaisEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val pupulation: Int,
    val capital: String,
    val flagUrl: String
)