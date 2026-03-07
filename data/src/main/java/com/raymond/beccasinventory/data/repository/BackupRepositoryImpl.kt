package com.raymond.beccasinventory.data.repository

import com.raymond.beccasinventory.contracts.repository.BackupRepository
import com.raymond.beccasinventory.data.backup.BackupData
import com.raymond.beccasinventory.data.local.dao.BackupDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val backupDao: BackupDao
) : BackupRepository {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun createBackup(): ByteArray = withContext(Dispatchers.IO) {
        val backupData = BackupData(
            inventoryItems = backupDao.getAllInventoryItems(),
            tags = backupDao.getAllTags(),
            inventoryItemTagCrossRefs = backupDao.getAllInventoryItemTagCrossRefs()
        )
        // Serialize Database models to pure CBOR binary format
        Cbor.encodeToByteArray(backupData)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun restoreBackup(data: ByteArray): Unit = withContext(Dispatchers.IO) {
        // Decode binary to BackupData structure
        val backupData = Cbor.decodeFromByteArray<BackupData>(data)
        
        // Push parsed snapshot to database
        backupDao.wipeDatabase()
        backupDao.insertInventoryItems(backupData.inventoryItems)
        backupDao.insertTags(backupData.tags)
        backupDao.insertInventoryItemTagCrossRefs(backupData.inventoryItemTagCrossRefs)
    }
}


