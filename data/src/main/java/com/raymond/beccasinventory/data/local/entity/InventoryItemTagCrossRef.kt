package com.raymond.beccasinventory.data.local.entity

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "inventoryItem_tag_cross_ref",
    primaryKeys = ["inventoryItemId", "tagId"]
)
data class InventoryItemTagCrossRef(
    val inventoryItemId: Long,
    val tagId: Long
)


