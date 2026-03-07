package com.raymond.beccasinventory.models

import androidx.compose.runtime.Immutable

@Immutable
data class Tag(
    val id: Long = 0,
    val name: String
)

