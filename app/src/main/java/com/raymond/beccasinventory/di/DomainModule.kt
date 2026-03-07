package com.raymond.beccasinventory.di

import com.raymond.beccasinventory.contracts.usecase.InventoryItemUseCase
import com.raymond.beccasinventory.contracts.usecase.TagUseCase
import com.raymond.beccasinventory.domain.usecase.InventoryItemUseCaseImpl
import com.raymond.beccasinventory.domain.usecase.TagUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {

    @Binds
    @ViewModelScoped
    abstract fun bindInventoryItemUseCase(
        inventoryItemUseCaseImpl: InventoryItemUseCaseImpl
    ): InventoryItemUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindTagUseCase(
        tagUseCaseImpl: TagUseCaseImpl
    ): TagUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindBackupRestoreUseCase(
        backupRestoreUseCaseImpl: com.raymond.beccasinventory.domain.usecase.BackupRestoreUseCaseImpl
    ): com.raymond.beccasinventory.contracts.usecase.BackupRestoreUseCase
}


