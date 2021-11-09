package com.example.programrecording.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity
data class Program(

    @Json(name = "id")
    @PrimaryKey
    val id: Int,

    @Json(name = "duration")
    val duration: Int?,

    @Json(name = "poster")
    val poster: String?,

    @Json(name = "startTime")
    val startTime: Int?,

    @Json(name = "title")
    val title: String?

)