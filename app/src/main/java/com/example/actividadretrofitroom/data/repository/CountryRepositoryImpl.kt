package com.example.actividadretrofitroom.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.actividadretrofitroom.data.local.CountryDao
import com.example.actividadretrofitroom.data.mapper.toDomain
import com.example.actividadretrofitroom.data.mapper.toEntity
import com.example.actividadretrofitroom.data.remote.RestCountriesApi
import com.example.actividadretrofitroom.domain.model.Country
import com.example.actividadretrofitroom.domain.repository.CountryRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepositoryImpl @Inject constructor(
    private val api: RestCountriesApi,
    private val dao: CountryDao,
    private val moshi: Moshi,
) : CountryRepository {
    override fun pagedCountries(query: String?, region: String?): Flow<PagingData<Country>> =
        Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { dao.pagingFiltered(query?.takeIf { it.isNotBlank() }, region?.takeIf { it.isNotBlank() }) },
        ).flow.map { paging -> paging.map { it.toDomain(moshi) } }

    override suspend fun refreshAll() {
        val now = System.currentTimeMillis()
        val entities = api.getAllCountries().mapNotNull { it.toEntity(now, moshi) }
        dao.upsertAll(entities)
    }

    override suspend fun refreshByName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        val now = System.currentTimeMillis()
        val entities = api.searchByName(trimmed).mapNotNull { it.toEntity(now, moshi) }
        dao.upsertAll(entities)
    }

    override suspend fun refreshByRegion(region: String) {
        val trimmed = region.trim()
        if (trimmed.isBlank()) return
        val now = System.currentTimeMillis()
        val entities = api.filterByRegion(trimmed).mapNotNull { it.toEntity(now, moshi) }
        dao.upsertAll(entities)
    }

    override fun observeCountry(cca3: String): Flow<Country?> =
        dao.observeByCca3(cca3).map { it?.toDomain(moshi) }

    override suspend fun refreshDetail(cca3: String) {
        val now = System.currentTimeMillis()
        val entities = api.getByAlpha(cca3).mapNotNull { it.toEntity(now, moshi) }
        dao.upsertAll(entities)
    }
}

