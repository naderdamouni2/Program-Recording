package com.example.programrecording.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.programrecording.R
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Entity(tableName = "future_program_mock")
@Parcelize
data class FutureProgramMock(
    @PrimaryKey
    val id: Int = 0,
    val duration: String,
    val channelPoster: Int,
    val programPoster: Int,
    var startTime: String,
    val title: String
) : Parcelable

val futureProgramsList = listOf(
    FutureProgramMock(
        1,
        "120",
        R.drawable.ic_fox,
        R.drawable.ic_nba,
        "12pm",
        "NBA"
    ),
    FutureProgramMock(
        2,
        "120",
        R.drawable.ic_bravo,
        R.drawable.ic_raw,
        "1pm",
        "RAW"
    ),
    FutureProgramMock(
        3,
        "120",
        R.drawable.ic_usa,
        R.drawable.ic_nfl,
        "2pm",
        "NFL"
    ),
    FutureProgramMock(
        4,
        "120",
        R.drawable.ic_bet_network,
        R.drawable.ic_mlb,
        "3pm",
        "MLB"
    ),
    FutureProgramMock(
        5,
        "120",
        R.drawable.ic_fox,
        R.drawable.ic_nba,
        "4pm",
        "NBA"
    ),
    FutureProgramMock(
        6,
        "120",
        R.drawable.ic_bravo,
        R.drawable.ic_raw,
        "5pm",
        "RAW"
    ),
)
