package dev.tenx.fxmobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tenx.fxmobile.data.local.storage.StorageManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideStorageManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): StorageManager {
        return StorageManager(context)
    }
}
