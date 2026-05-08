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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, 
            "paises_db"
        ).fallbackToDestructiveMigration() // Útil durante el desarrollo si cambias la entidad
        .build()
    }

    @Provides
    fun providePaisDao(database: AppDatabase): PaisDao {
        return database.paisDao()
    }
}
