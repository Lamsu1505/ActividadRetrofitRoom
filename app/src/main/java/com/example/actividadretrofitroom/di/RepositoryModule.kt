package com.example.actividadretrofitroom.di

import com.example.actividadretrofitroom.data.repository.CountryRepositoryImpl
import com.example.actividadretrofitroom.domain.repository.CountryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCountryRepository(impl: CountryRepositoryImpl): CountryRepository
}

