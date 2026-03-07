package com.raymond.beccasinventory.data.repository

import com.raymond.beccasinventory.contracts.repository.TagRepository
import com.raymond.beccasinventory.data.local.dao.TagDao
import com.raymond.beccasinventory.data.local.entity.TagEntity
import com.raymond.beccasinventory.models.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getTagById(id: Long): Tag? {
        return tagDao.getTagById(id)?.toModel()
    }

    override suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(TagEntity.fromModel(tag))
    }

    override suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(TagEntity.fromModel(tag))
    }
}

