package com.raymond.beccasinventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raymond.beccasinventory.models.InventoryItem
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "inventoryItem_entries")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: Int = 0,
    val imageUri: String?
) {
    fun toModel() = InventoryItem(
        id = id,
        name = name,
        quantity = quantity,
        imageUri = imageUri
    )

    companion object {
        fun fromModel(model: InventoryItem) = InventoryItemEntity(
            id = model.id,
            name = model.name,
            quantity = model.quantity,
            imageUri = model.imageUri
        )
    }
}


