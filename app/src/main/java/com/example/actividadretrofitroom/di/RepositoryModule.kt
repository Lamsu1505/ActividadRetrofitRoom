package com.example.actividadretrofitroom.di

import com.example.actividadretrofitroom.Data.Repository.PaisRepository
import com.example.actividadretrofitroom.Data.Repository.PaisRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPaisRepository(
        paisRepositoryImpl: PaisRepositoryImpl
    ): PaisRepository
}