package dev.tenx.fxmobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tenx.fxmobile.data.remote.KiloRepository
import dev.tenx.fxmobile.data.remote.PreferencesManager
import dev.tenx.fxmobile.data.local.db.SessionDao
import dev.tenx.fxmobile.data.local.db.MessageDao
import dev.tenx.fxmobile.data.repository.SessionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSessionRepository(
        sessionDao: SessionDao,
        messageDao: MessageDao,
        kiloRepository: KiloRepository,
        preferencesManager: PreferencesManager
    ): SessionRepository {
        return SessionRepository(
            sessionDao = sessionDao,
            messageDao = messageDao,
            kiloRepository = kiloRepository,
            preferencesManager = preferencesManager
        )
    }
}
