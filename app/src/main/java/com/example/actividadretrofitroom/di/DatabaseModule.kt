package com.example.actividadretrofitroom.di

import android.content.Context
import androidx.room.Room
import com.example.actividadretrofitroom.Data.Local.AppDatabase
import com.example.actividadretrofitroom.Data.Local.DAO.PaisDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // Proporciona la instancia de la base de datos
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    @Provides
    fun providePaisDao(database: AppDatabase): PaisDao {
        // Se inicializa el DAO a partir de la base de datos
        return database.paisDao()
    }
}