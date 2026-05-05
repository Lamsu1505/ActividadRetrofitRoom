package com.example.actividadretrofitroom.Data.Local.Repository

import com.example.actividadretrofitroom.Data.Local.DAO.PaisDao
import com.example.actividadretrofitroom.Data.Local.Entity.PaisEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PaisRepository @Inject constructor(
    private val paisDao: PaisDao
) {

    suspend fun insert(pais: PaisEntity) {
        paisDao.insert(pais)
    }

    suspend fun getAll(): List<PaisEntity> {
        return paisDao.getAll()
    }
}