package com.example.programrecording.di

import android.content.Context
import com.example.programrecording.repo.local.ProgramDao
import com.example.programrecording.repo.local.ProgramDatabase
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
    fun provideProgramDatabase(
        @ApplicationContext context: Context
    ) : ProgramDatabase = ProgramDatabase.getInstance(context)

    @Provides
    fun providesProgramDao(database: ProgramDatabase): ProgramDao {
        return database.programDao()
    }
}