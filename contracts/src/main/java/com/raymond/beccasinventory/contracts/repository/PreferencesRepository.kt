package com.raymond.beccasinventory.contracts.repository

import com.raymond.beccasinventory.models.AppTheme
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val appTheme: Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)
}

