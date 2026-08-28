package dev.tenx.fxmobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.tenx.fxmobile.data.local.db.FxDatabase
import dev.tenx.fxmobile.data.local.db.SessionDao
import dev.tenx.fxmobile.data.local.db.MessageDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FxDatabase {
        return Room.databaseBuilder(
            context,
            FxDatabase::class.java,
            FxDatabase.NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSessionDao(database: FxDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideMessageDao(database: FxDatabase): MessageDao = database.messageDao()
}
