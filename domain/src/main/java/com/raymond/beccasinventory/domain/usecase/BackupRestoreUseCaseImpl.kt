package com.raymond.beccasinventory.domain.usecase

import com.raymond.beccasinventory.contracts.repository.BackupRepository
import com.raymond.beccasinventory.contracts.usecase.BackupRestoreUseCase
import javax.inject.Inject

class BackupRestoreUseCaseImpl @Inject constructor(
    private val backupRepository: BackupRepository
) : BackupRestoreUseCase {
    override suspend fun exportData(): ByteArray {
        return backupRepository.createBackup()
    }

    override suspend fun importData(data: ByteArray) {
        backupRepository.restoreBackup(data)
    }
}

