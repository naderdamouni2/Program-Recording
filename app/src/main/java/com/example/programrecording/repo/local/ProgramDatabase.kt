package com.example.programrecording.repo.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.programrecording.model.FutureProgramMock
import com.example.programrecording.model.ProgramMock

@Database(entities = [ProgramMock::class, FutureProgramMock::class], version = 1, exportSchema = false)
abstract class ProgramDatabase : RoomDatabase() {

    abstract fun programDao(): ProgramDao

    companion object {

        private const val DATABASE_NAME = "program.db"

        @Volatile
        private var INSTANCE: ProgramDatabase? = null

        fun getInstance(context: Context): ProgramDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProgramDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}