package com.example.actividadretrofitroom.di

import android.content.Context
import androidx.room.Room
import com.example.actividadretrofitroom.data.local.AppDatabase
import com.example.actividadretrofitroom.data.local.CountryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCountryDao(db: AppDatabase): CountryDao = db.countryDao()
}

