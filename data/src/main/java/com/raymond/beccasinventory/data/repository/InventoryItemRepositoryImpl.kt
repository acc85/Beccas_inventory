package com.raymond.beccasinventory.data.repository

import com.raymond.beccasinventory.contracts.repository.InventoryItemRepository
import com.raymond.beccasinventory.data.local.dao.InventoryItemDao
import com.raymond.beccasinventory.data.local.entity.InventoryItemEntity
import com.raymond.beccasinventory.data.local.entity.InventoryItemTagCrossRef
import com.raymond.beccasinventory.data.local.dao.TagDao
import com.raymond.beccasinventory.data.local.entity.TagEntity
import com.raymond.beccasinventory.models.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class InventoryItemRepositoryImpl @Inject constructor(
    private val inventoryItemDao: InventoryItemDao,
    private val tagDao: TagDao
) : InventoryItemRepository {
    override fun getAllInventoryItems(): Flow<List<InventoryItem>> {
        return inventoryItemDao.getAllInventoryItems().map { relations ->
            relations.map { relation ->
                relation.inventoryItem.toModel().copy(
                    tags = relation.tags.map { it.toModel() }.toImmutableList()
                )
            }
        }
    }

    override suspend fun getInventoryItemById(id: Long): InventoryItem? {
        val relation = inventoryItemDao.getInventoryItemById(id) ?: return null
        return relation.inventoryItem.toModel().copy(
            tags = relation.tags.map { it.toModel() }.toImmutableList()
        )
    }

    override suspend fun insertInventoryItem(inventoryItem: InventoryItem): Long {
        val id = inventoryItemDao.insertInventoryItem(InventoryItemEntity.fromModel(inventoryItem))
        insertTagsForInventoryItem(id, inventoryItem)
        return id
    }

    override suspend fun updateInventoryItem(inventoryItem: InventoryItem) {
        inventoryItemDao.updateInventoryItem(InventoryItemEntity.fromModel(inventoryItem))
        inventoryItemDao.deleteTagsForInventoryItem(inventoryItem.id)
        insertTagsForInventoryItem(inventoryItem.id, inventoryItem)
    }

    override suspend fun updateInventoryItemQuantity(inventoryItem: InventoryItem) {
        inventoryItemDao.updateInventoryItem(InventoryItemEntity.fromModel(inventoryItem))
    }


    override suspend fun deleteInventoryItem(inventoryItem: InventoryItem) {
        inventoryItemDao.deleteInventoryItem(InventoryItemEntity.fromModel(inventoryItem))
    }

    override suspend fun deleteMultipleInventoryItems(inventoryItems: List<InventoryItem>) {
        inventoryItemDao.deleteMultipleInventoryItems(inventoryItems.map { InventoryItemEntity.fromModel(it) })
    }

    private suspend fun insertTagsForInventoryItem(inventoryItemId: Long, inventoryItem: InventoryItem) {
        inventoryItem.tags.forEach { tag ->
            var tagId = tag.id
            if (tagId == 0L) {
                tagId = tagDao.insertTag(TagEntity.fromModel(tag))
            }
            inventoryItemDao.insertInventoryItemTagCrossRef(
                InventoryItemTagCrossRef(
                    inventoryItemId = inventoryItemId,
                    tagId = tagId
                )
            )
        }
    }
}


