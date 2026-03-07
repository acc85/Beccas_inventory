package com.raymond.beccasinventory.ui.screens

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymond.beccasinventory.contracts.repository.PreferencesRepository
import com.raymond.beccasinventory.contracts.usecase.BackupRestoreUseCase
import com.raymond.beccasinventory.models.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val backupRestoreUseCase: BackupRestoreUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = preferencesRepository.appTheme
        .map { SettingsUiState(theme = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.setAppTheme(theme)
        }
    }

    fun consumeMessage() {
        // Just clear the message after showing
    }

    fun exportDatabaseToUri(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                // Should show loading state, but for simplicity we rely on fast execution
                val binaryData = backupRestoreUseCase.exportData()
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(binaryData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importDatabaseFromUri(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val binaryData = inputStream.readBytes()
                    backupRestoreUseCase.importData(binaryData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

