package com.raymond.beccasinventory.contracts.repository

interface BackupRepository {
    suspend fun createBackup(): ByteArray
    suspend fun restoreBackup(data: ByteArray)
}

