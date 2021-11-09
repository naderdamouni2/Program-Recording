package com.example.programrecording.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.programrecording.R
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
@Entity(tableName = "programs_mock")
data class ProgramMock(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val network: String,
    val duration: String,
    val channelPoster: Int,
    val programPoster: Int,
    var startTime: String,
    val title: String
) : Parcelable

val programList = listOf(
    ProgramMock(
        1,
        "FOX",
        "120",
        R.drawable.ic_fox,
        R.drawable.ic_nba,
        "12pm",
        "NBA"
    ),
    ProgramMock(
        2,
        "TNT",
        "120",
        R.drawable.ic_tnt,
        R.drawable.ic_raw,
        "1pm",
        "RAW"
    ),
    ProgramMock(
        3,
        "USA",
        "120",
        R.drawable.ic_usa,
        R.drawable.ic_nfl,
        "2pm",
        "NFL"
    ),
    ProgramMock(
        4,
        "BET",
        "120",
        R.drawable.ic_bet_network,
        R.drawable.ic_mlb,
        "3pm",
        "MLB"
    ),
    ProgramMock(
        5,
        "FOX",
        "120",
        R.drawable.ic_fox,
        R.drawable.ic_nba,
        "4pm",
        "NBA"
    ),
    ProgramMock(
        6,
        "bravo",
        "120",
        R.drawable.ic_bravo,
        R.drawable.ic_raw,
        "5pm",
        "RAW"
    ),
    ProgramMock(
        7,
        "USA",
        "120",
        R.drawable.ic_usa,
        R.drawable.ic_nfl,
        "6pm",
        "NFL"
    ),
    ProgramMock(
        8,
        "BET",
        "120",
        R.drawable.ic_bet_network,
        R.drawable.ic_mlb,
        "7pm",
        "MLB"
    ),
    ProgramMock(
        9,
        "BET",
        "120",
        R.drawable.ic_fox,
        R.drawable.ic_nba,
        "8pm",
        "NBA"
    ),
    ProgramMock(
        10,
        "TNT",
        "120",
        R.drawable.ic_tnt,
        R.drawable.ic_nfl,
        "9pm",
        "NFL"
    )
)