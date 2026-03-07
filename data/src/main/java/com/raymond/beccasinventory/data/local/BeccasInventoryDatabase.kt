package com.raymond.beccasinventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raymond.beccasinventory.data.local.dao.BackupDao
import com.raymond.beccasinventory.data.local.dao.InventoryItemDao
import com.raymond.beccasinventory.data.local.dao.TagDao
import com.raymond.beccasinventory.data.local.entity.InventoryItemEntity
import com.raymond.beccasinventory.data.local.entity.InventoryItemTagCrossRef
import com.raymond.beccasinventory.data.local.entity.TagEntity

@Database(
    entities = [
        InventoryItemEntity::class,
        TagEntity::class,
        InventoryItemTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BeccasInventoryDatabase : RoomDatabase() {
    abstract val inventoryItemDao: InventoryItemDao
    abstract val tagDao: TagDao
    abstract val backupDao: BackupDao
}
