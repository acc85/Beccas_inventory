package com.raymond.beccasinventory.contracts.usecase

interface BackupRestoreUseCase {
    suspend fun exportData(): ByteArray
    suspend fun importData(data: ByteArray)
}

