package com.raymond.beccasinventory.contracts.usecase

import com.raymond.beccasinventory.models.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryItemUseCase {
    fun observeInventoryItems(): Flow<List<InventoryItem>>
    suspend fun getInventoryItem(id: Long): InventoryItem?
    suspend fun saveInventoryItem(inventoryItem: InventoryItem)
    suspend fun updateInventoryItemQuantity(inventoryItem: InventoryItem)
    suspend fun deleteInventoryItem(inventoryItem: InventoryItem)
    suspend fun deleteMultipleInventoryItems(inventoryItems: List<InventoryItem>)
}


