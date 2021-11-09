package com.example.programrecording.download

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DownloadPrefManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore by lazy { context.dataStore }

    val isDownloading get() = dataStore.data.map { it[KEY_DOWNLOADING] ?: false }

    suspend fun setDownloading(isDownloading: Boolean) {
        dataStore.edit { it[KEY_DOWNLOADING] = isDownloading }
    }

    companion object {
        private val KEY_DOWNLOADING = booleanPreferencesKey("downloading")
    }
}