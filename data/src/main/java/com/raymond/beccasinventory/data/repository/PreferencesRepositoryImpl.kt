package com.raymond.beccasinventory.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.raymond.beccasinventory.contracts.repository.PreferencesRepository
import com.raymond.beccasinventory.models.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    override val appTheme: Flow<AppTheme> = dataStore.data.map { preferences ->
        val themeString = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name
        try {
            AppTheme.valueOf(themeString)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM
        }
    }

    override suspend fun setAppTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }
}

