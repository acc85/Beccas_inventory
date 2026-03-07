package com.raymond.beccasinventory.contracts.repository

import com.raymond.beccasinventory.models.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagById(id: Long): Tag?
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
}

