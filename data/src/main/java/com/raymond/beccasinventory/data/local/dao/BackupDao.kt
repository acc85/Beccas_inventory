package com.raymond.beccasinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.raymond.beccasinventory.data.local.entity.InventoryItemEntity
import com.raymond.beccasinventory.data.local.entity.InventoryItemTagCrossRef
import com.raymond.beccasinventory.data.local.entity.TagEntity

@Dao
interface BackupDao {

    @Query("SELECT * FROM inventoryItem_entries")
    suspend fun getAllInventoryItems(): List<InventoryItemEntity>

    @Query("SELECT * FROM tags")
    suspend fun getAllTags(): List<TagEntity>

    @Query("SELECT * FROM inventoryItem_tag_cross_ref")
    suspend fun getAllInventoryItemTagCrossRefs(): List<InventoryItemTagCrossRef>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(inventoryItems: List<InventoryItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItemTagCrossRefs(refs: List<InventoryItemTagCrossRef>)

    @Query("DELETE FROM inventoryItem_entries")
    suspend fun clearInventoryItems()

    @Query("DELETE FROM tags")
    suspend fun clearTags()

    @Query("DELETE FROM inventoryItem_tag_cross_ref")
    suspend fun clearInventoryItemTagCrossRefs()

    @Transaction
    suspend fun wipeDatabase() {
        clearInventoryItemTagCrossRefs()
        clearInventoryItems()
        clearTags()
    }
}


