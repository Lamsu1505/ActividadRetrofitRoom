package com.example.actividadretrofitroom.Data.Repository

import com.example.actividadretrofitroom.Data.Local.DAO.PaisDao
import com.example.actividadretrofitroom.Data.Local.Entity.toDomain
import com.example.actividadretrofitroom.Data.Local.Entity.toEntity
import com.example.actividadretrofitroom.Data.Remote.Dto.toDomainDetail
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

    override suspend fun getAllCountries(): Result<List<CountryListItem>> {
        return try {
            val response = apiService.getAllCountries()
            val domainList = response.map { it.toDomainListItem() }
            
            // Persistencia local: Actualizar caché de forma atómica
            paisDao.updateAll(domainList.map { it.toEntity() })
            
            Result.success(domainList)
        } catch (e: Exception) {
            // Recuperación Offline: Si falla la red, buscar en Room
            val localPaises = paisDao.getAll()
            if (localPaises.isNotEmpty()) {
                Result.success(localPaises.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCountryByCode(code: String): Result<CountryDetail> {
        return try {
            val response = apiService.getByCode(code)
            val country = response.firstOrNull()
            if (country != null) {
                val detail = country.toDomainDetail()
                // Guardar en Room solo cuando se visualiza el detalle
                paisDao.insertDetail(detail.toEntity())
                Result.success(detail)
            } else {
                Result.failure(Exception("País no encontrado"))
            }
        } catch (e: Exception) {
            // Intentar recuperar de la caché de detalles si no hay conexión
            val localDetail = paisDao.getDetailByCode(code)
            if (localDetail != null) {
                Result.success(localDetail.toDomain())
            } else {
                // Caso solicitado: Sin internet y sin datos en Room
                Result.failure(Exception("no tienes conexion a internet y tampoco esta guardado en el cache"))
            }
        }
    }

    override suspend fun getCountriesByRegion(region: String): Result<List<CountryListItem>> {
        return try {
            val response = apiService.getByRegion(region)
            val domainList = response.map { it.toDomainListItem() }
            Result.success(domainList)
        } catch (e: Exception) {
            val localPaises = paisDao.getByRegion(region)
            if (localPaises.isNotEmpty()) {
                Result.success(localPaises.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun searchCountries(query: String): Result<List<CountryListItem>> {
        return try {
            val response = apiService.searchByName(query)
            Result.success(response.map { it.toDomainListItem() })
        } catch (e: Exception) {
            val localPaises = paisDao.search(query)
            if (localPaises.isNotEmpty()) {
                Result.success(localPaises.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }
}
