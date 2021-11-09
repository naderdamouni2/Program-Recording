package com.example.programrecording.repo.local

import androidx.room.*
import com.example.programrecording.model.FutureProgramMock
import com.example.programrecording.model.ProgramMock
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {

    //PROGRAMS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(vararg program: ProgramMock)

    @Update
    suspend fun updateProgram(program: ProgramMock)

    @Delete
    suspend fun deleteProgram(program: ProgramMock)

    @Query("DELETE FROM programs_mock")
    suspend fun deleteAllPrograms()

    @Query("SELECT * FROM programs_mock")
    fun getAllPrograms(): Flow<List<ProgramMock>>


    // Future Programs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuturePrograms(vararg futureProgramMock: FutureProgramMock)

    @Update
    suspend fun updateFuturePrograms(futureProgramMock: FutureProgramMock)

    @Delete
    suspend fun deleteFuturePrograms(futureProgramMock: FutureProgramMock)

    @Query("DELETE FROM future_program_mock")
    suspend fun deleteAllFuturePrograms()

    @Query("SELECT * FROM future_program_mock")
    fun getAllFuturePrograms(): Flow<List<FutureProgramMock>>
}