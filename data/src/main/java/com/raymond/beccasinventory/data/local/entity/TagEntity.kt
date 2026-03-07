package com.raymond.beccasinventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raymond.beccasinventory.models.Tag
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
) {
    fun toModel() = Tag(
        id = id,
        name = name
    )

    companion object {
        fun fromModel(model: Tag) = TagEntity(
            id = model.id,
            name = model.name
        )
    }
}

