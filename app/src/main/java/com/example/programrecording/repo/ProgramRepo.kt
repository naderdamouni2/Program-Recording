package com.example.programrecording.repo

import com.example.programrecording.model.FutureProgramMock
import com.example.programrecording.model.ProgramMock
import com.example.programrecording.model.futureProgramsList
import com.example.programrecording.model.programList
import com.example.programrecording.repo.local.ProgramDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProgramRepo @Inject constructor(
    private val programDao: ProgramDao
) {

    suspend fun getProgramListMock(): Flow<List<ProgramMock>> {
        delay(2000)
        val programFlow = programDao.getAllPrograms()
        if (!programList.isNullOrEmpty()) programDao.insertPrograms(*programList.toTypedArray())
        return programFlow
    }

    suspend fun getFutureProgramListMock(): Flow<List<FutureProgramMock>> {
        delay(2000)
        val timeSlotFlow = programDao.getAllFuturePrograms()
        if (!futureProgramsList.isNullOrEmpty())
            programDao.insertFuturePrograms(*futureProgramsList.toTypedArray())
        return timeSlotFlow
    }

}