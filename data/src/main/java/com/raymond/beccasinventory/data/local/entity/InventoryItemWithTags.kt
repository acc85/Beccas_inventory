package com.raymond.beccasinventory.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class InventoryItemWithTags(
    @Embedded val inventoryItem: InventoryItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = InventoryItemTagCrossRef::class,
            parentColumn = "inventoryItemId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)


