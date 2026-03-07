package com.raymond.beccasinventory.contracts.repository

import com.raymond.beccasinventory.models.AppTheme
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val appTheme: Flow<AppTheme>
    val isLocked: Flow<Boolean>
    suspend fun setAppTheme(theme: AppTheme)
    suspend fun setLocked(locked: Boolean)
}

