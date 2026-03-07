package com.raymond.beccasinventory.contracts.usecase

import com.raymond.beccasinventory.models.Tag
import kotlinx.coroutines.flow.Flow

interface TagUseCase {
    fun observeTags(): Flow<List<Tag>>
    suspend fun getTag(id: Long): Tag?
    suspend fun saveTag(tag: Tag)
    suspend fun deleteTag(tag: Tag)
}

