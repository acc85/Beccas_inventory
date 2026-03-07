package com.raymond.beccasinventory.contracts.repository

import com.raymond.beccasinventory.models.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryItemRepository {
    fun getAllInventoryItems(): Flow<List<InventoryItem>>
    suspend fun getInventoryItemById(id: Long): InventoryItem?
    suspend fun insertInventoryItem(inventoryItem: InventoryItem): Long
    suspend fun updateInventoryItem(inventoryItem: InventoryItem)
    suspend fun updateInventoryItemQuantity(inventoryItem: InventoryItem)
    suspend fun deleteInventoryItem(inventoryItem: InventoryItem)
    suspend fun deleteMultipleInventoryItems(inventoryItems: List<InventoryItem>)
}


