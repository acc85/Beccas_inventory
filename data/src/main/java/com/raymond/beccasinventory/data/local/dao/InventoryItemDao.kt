package com.raymond.beccasinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.raymond.beccasinventory.data.local.entity.InventoryItemEntity
import com.raymond.beccasinventory.data.local.entity.InventoryItemTagCrossRef
import com.raymond.beccasinventory.data.local.entity.InventoryItemWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Transaction
    @Query("SELECT * FROM inventoryItem_entries")
    fun getAllInventoryItems(): Flow<List<InventoryItemWithTags>>

    @Transaction
    @Query("SELECT * FROM inventoryItem_entries WHERE id = :id")
    suspend fun getInventoryItemById(id: Long): InventoryItemWithTags?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(inventoryItem: InventoryItemEntity): Long

    @Update
    suspend fun updateInventoryItem(inventoryItem: InventoryItemEntity)

    @Delete
    suspend fun deleteInventoryItem(inventoryItem: InventoryItemEntity)

    @Delete
    suspend fun deleteMultipleInventoryItems(inventoryItems: List<InventoryItemEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInventoryItemTagCrossRef(crossRef: InventoryItemTagCrossRef)

    @Query("DELETE FROM inventoryItem_tag_cross_ref WHERE inventoryItemId = :inventoryItemId")
    suspend fun deleteTagsForInventoryItem(inventoryItemId: Long)
}


