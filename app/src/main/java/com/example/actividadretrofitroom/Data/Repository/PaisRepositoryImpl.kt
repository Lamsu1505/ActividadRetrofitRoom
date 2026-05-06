package com.example.actividadretrofitroom.Data.Repository

import com.example.actividadretrofitroom.Data.Local.DAO.PaisDao
import com.example.actividadretrofitroom.Data.Local.Entity.PaisEntity
import com.example.actividadretrofitroom.Data.Remote.Dto.toDomainListItem
import com.example.actividadretrofitroom.Data.Remote.PaisApiService
import com.example.actividadretrofitroom.Domain.CountryDetail
import com.example.actividadretrofitroom.Domain.CountryListItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaisRepositoryImpl @Inject constructor(
    private val paisDao: PaisDao,
    private val apiService: PaisApiService
) : PaisRepository {

    override suspend fun insert(pais: PaisEntity) {
        paisDao.insert(pais)
    }

    override suspend fun getAllLocal(): List<PaisEntity> {
        return paisDao.getAll()
    }

    override suspend fun getAllCountries(): Result<List<CountryListItem>> {
        return try {
            val response = apiService.getAllCountries()
            Result.success(response.map { it.toDomainListItem() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCountryByCode(code: String): Result<CountryDetail> {
        TODO("Not yet implemented")
    }

    override suspend fun getCountriesByRegion(region: String): Result<List<CountryListItem>> {
        TODO("Not yet implemented")
    }

//    override suspend fun getCountryByCode(code: String): Result<CountryDetail> {
//        return try {
//            val response = apiService.getCountryByCode(code)
//            // La API devuelve una lista, tomamos el primer resultado
//            val country = response.firstOrNull()
//            if (country != null) {
//                Result.success(country.toDomainDetail())
//            } else {
//                Result.failure(Exception("País no encontrado"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

//    override suspend fun getCountriesByRegion(region: String): Result<List<CountryListItem>> {
//        return try {
//            val response = apiService.getCountriesByRegion(region)
//            Result.success(response.map { it.toDomainListItem() })
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}