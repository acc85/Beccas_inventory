package com.raymond.beccasinventory.di

import com.raymond.beccasinventory.contracts.repository.InventoryItemRepository
import com.raymond.beccasinventory.contracts.repository.TagRepository
import com.raymond.beccasinventory.data.repository.InventoryItemRepositoryImpl
import com.raymond.beccasinventory.data.repository.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInventoryItemRepository(
        inventoryItemRepositoryImpl: InventoryItemRepositoryImpl
    ): InventoryItemRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: com.raymond.beccasinventory.data.repository.PreferencesRepositoryImpl
    ): com.raymond.beccasinventory.contracts.repository.PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        backupRepositoryImpl: com.raymond.beccasinventory.data.repository.BackupRepositoryImpl
    ): com.raymond.beccasinventory.contracts.repository.BackupRepository
}


