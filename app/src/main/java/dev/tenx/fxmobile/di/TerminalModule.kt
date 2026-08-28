package dev.tenx.fxmobile.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.tenx.fxmobile.terminal.ShellExecutor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TerminalModule {

    @Provides
    @Singleton
    fun provideShellExecutor(): ShellExecutor = ShellExecutor()
}
