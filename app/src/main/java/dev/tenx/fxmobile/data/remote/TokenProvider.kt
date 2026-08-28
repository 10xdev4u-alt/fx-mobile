package dev.tenx.fxmobile.data.remote

import javax.inject.Inject
import javax.inject.Singleton

interface TokenProvider {
    suspend fun getToken(): String?
    suspend fun setToken(token: String)
    suspend fun clearToken()
}

@Singleton
class TokenProviderImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : TokenProvider {

    override suspend fun getToken(): String? = preferencesManager.getApiToken()

    override suspend fun setToken(token: String) = preferencesManager.setApiToken(token)

    override suspend fun clearToken() = preferencesManager.clearApiToken()
}
