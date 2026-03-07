package com.raymond.beccasinventory.domain.usecase

import com.raymond.beccasinventory.contracts.repository.TagRepository
import com.raymond.beccasinventory.contracts.usecase.TagUseCase
import com.raymond.beccasinventory.models.Tag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagUseCaseImpl @Inject constructor(
    private val repository: TagRepository
) : TagUseCase {

    override fun observeTags(): Flow<List<Tag>> {
        return repository.getAllTags()
    }

    override suspend fun getTag(id: Long): Tag? {
        return repository.getTagById(id)
    }

    override suspend fun saveTag(tag: Tag) {
        if (tag.id == 0L) {
            repository.insertTag(tag)
        }
    }

    override suspend fun deleteTag(tag: Tag) {
        repository.deleteTag(tag)
    }
}

