package com.example.programrecording.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.programrecording.model.FutureProgramMock
import com.example.programrecording.model.ProgramMock
import com.example.programrecording.repo.ProgramRepo
import com.example.programrecording.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramViewModel @Inject constructor(
    private val programRepo: ProgramRepo
) : ViewModel() {

    /** MOCK DATA **/

    private val _programListMock = MutableLiveData<ApiState<List<ProgramMock>>>()
    val programListMock: LiveData<ApiState<List<ProgramMock>>> get() = _programListMock

    private val _futureProgramListMock = MutableLiveData<ApiState<List<FutureProgramMock>>>()
    val futureProgramListMock: LiveData<ApiState<List<FutureProgramMock>>> get() = _futureProgramListMock

    init {
        getProgramListMock()
    }

    fun getProgramListMock() {
        viewModelScope.launch {
            try {
                programRepo.getProgramListMock().collect { programState ->
                    val state = if (programState.isNullOrEmpty())
                        ApiState.Failure("PROGRAM STATE IS NULL OR EMPTY")
                    else ApiState.Success(programState)
                    _programListMock.postValue(state)
                }
            } catch (error: Exception) {
                _programListMock.postValue(ApiState.Failure("PROGRAMS EXCEPTION ERROR --> $error"))
            }
        }
    }

    fun getFutureProgramListMock() {
        viewModelScope.launch {
            try {
                programRepo.getFutureProgramListMock().collect { timeState ->
                    val state = if (timeState.isNullOrEmpty())
                        ApiState.Failure("FUTURE PROGRAM LIST IS NULL OR EMPTY")
                    else ApiState.Success(timeState)
                    _futureProgramListMock.postValue(state)
                }
            } catch (error: Exception) {
                _futureProgramListMock.postValue(ApiState.Failure("FUTURE PROGRAM LIST EXCEPTION ERROR --> $error"))
            }
        }
    }
}
