package com.raymond.beccasinventory.domain.usecase

import com.raymond.beccasinventory.contracts.repository.InventoryItemRepository
import com.raymond.beccasinventory.contracts.usecase.InventoryItemUseCase
import com.raymond.beccasinventory.models.InventoryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InventoryItemUseCaseImpl @Inject constructor(
    private val repository: InventoryItemRepository
) : InventoryItemUseCase {

    override fun observeInventoryItems(): Flow<List<InventoryItem>> {
        return repository.getAllInventoryItems()
    }

    override suspend fun getInventoryItem(id: Long): InventoryItem? {
        return repository.getInventoryItemById(id)
    }

    override suspend fun saveInventoryItem(inventoryItem: InventoryItem) {
        if (inventoryItem.id == 0L) {
            repository.insertInventoryItem(inventoryItem)
        } else {
            repository.updateInventoryItem(inventoryItem)
        }
    }

    override suspend fun updateInventoryItemQuantity(inventoryItem: InventoryItem) {
        repository.updateInventoryItemQuantity(inventoryItem)
    }

    override suspend fun deleteInventoryItem(inventoryItem: InventoryItem) {
        repository.deleteInventoryItem(inventoryItem)
    }

    override suspend fun deleteMultipleInventoryItems(inventoryItems: List<InventoryItem>) {
        repository.deleteMultipleInventoryItems(inventoryItems)
    }
}


