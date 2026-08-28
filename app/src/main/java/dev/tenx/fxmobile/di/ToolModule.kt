package dev.tenx.fxmobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tenx.fxmobile.terminal.ShellExecutor
import dev.tenx.fxmobile.tools.FileListTool
import dev.tenx.fxmobile.tools.FileReadTool
import dev.tenx.fxmobile.tools.FileWriteTool
import dev.tenx.fxmobile.tools.ShellTool
import dev.tenx.fxmobile.tools.ToolRegistry
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ToolModule {

    @Provides
    @Singleton
    fun provideToolRegistry(shellExecutor: ShellExecutor): ToolRegistry {
        val tools = listOf(
            ShellTool(shellExecutor),
            FileReadTool(),
            FileWriteTool(),
            FileListTool()
        )
        return ToolRegistry(tools)
    }
}
