package com.raymond.beccasinventory.di

import android.content.Context
import androidx.room.Room
import com.raymond.beccasinventory.data.local.BeccasInventoryDatabase
import com.raymond.beccasinventory.data.local.dao.BackupDao
import com.raymond.beccasinventory.data.local.dao.InventoryItemDao
import com.raymond.beccasinventory.data.local.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBeccasInventoryDatabase(@ApplicationContext context: Context): BeccasInventoryDatabase {
        return Room.databaseBuilder(
            context,
            BeccasInventoryDatabase::class.java,
            "beccasinventory_database"
        ).build()
    }

    @Provides
    fun provideInventoryItemDao(database: BeccasInventoryDatabase): InventoryItemDao {
        return database.inventoryItemDao
    }


    @Provides
    fun provideTagDao(database: BeccasInventoryDatabase): TagDao {
        return database.tagDao
    }

    @Provides
    fun provideBackupDao(database: BeccasInventoryDatabase): BackupDao {
        return database.backupDao
    }
}



