package com.example.programrecording.utils

sealed class ApiState<out R> {
    data class Success<T>(val data : T) : ApiState<T>()
    data class Failure(val errorMsg: String) : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
}
