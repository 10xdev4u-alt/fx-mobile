package dev.tenx.fxmobile.bridge

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FxCoreBridge @Inject constructor() {
    init {
        System.loadLibrary("fx")
    }

    external fun init(): Int
    external fun deinit(): Int
    external fun version(): String
    external fun runAgent(prompt: String): String

    fun initialize(): Boolean {
        return init() == 0
    }

    fun shutdown(): Boolean {
        return deinit() == 0
    }

    fun getVersion(): String {
        return version()
    }

    fun runAgentPrompt(prompt: String): String {
        return runAgent(prompt)
    }
}
