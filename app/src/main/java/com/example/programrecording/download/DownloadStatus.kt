package com.example.programrecording.download

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

sealed class DownloadStatus : Serializable {
    data class Downloading(val progress: Int) : DownloadStatus()
    data class Failed(val message: String) : DownloadStatus()
    object Canceled : DownloadStatus()
    object Success : DownloadStatus()
}

@Parcelize
data class DownloadRequest(val id: Int, val title: String) : Parcelable