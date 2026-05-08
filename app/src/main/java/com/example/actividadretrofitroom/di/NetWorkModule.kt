package com.example.actividadretrofitroom.di

import android.content.Context
import com.example.actividadretrofitroom.Core.LiveNetworkMonitor
import com.example.actividadretrofitroom.Core.NetworkMonitor
import com.example.actividadretrofitroom.Data.Remote.PaisApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCountriesApiService(retrofit: Retrofit): PaisApiService {
        return retrofit.create(PaisApiService::class.java)
    }


    // En NetworkModule.kt
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return LiveNetworkMonitor(context)
    }
}