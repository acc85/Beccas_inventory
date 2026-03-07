package com.raymond.beccasinventory.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class InventoryItem(
    val id: Long = 0,
    val name: String,
    val quantity: Int = 0,
    val imageUri: String? = null,
    val tags: ImmutableList<Tag> = persistentListOf()
)
