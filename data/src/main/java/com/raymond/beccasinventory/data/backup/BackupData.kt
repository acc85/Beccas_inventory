package com.raymond.beccasinventory.data.backup

import com.raymond.beccasinventory.data.local.entity.InventoryItemEntity
import com.raymond.beccasinventory.data.local.entity.InventoryItemTagCrossRef
import com.raymond.beccasinventory.data.local.entity.TagEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val inventoryItems: List<InventoryItemEntity> = emptyList(),
    val tags: List<TagEntity> = emptyList(),
    val inventoryItemTagCrossRefs: List<InventoryItemTagCrossRef> = emptyList()
)


