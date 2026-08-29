package dev.tenx.fxmobile.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fx_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val apiTokenKey = stringPreferencesKey("api_token")
    private val modelKey = stringPreferencesKey("model_name")
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val autoSaveKey = booleanPreferencesKey("auto_save_sessions")

    suspend fun getApiToken(): String? = context.dataStore.data
        .map { preferences -> preferences[apiTokenKey] }
        .first()

    suspend fun setApiToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[apiTokenKey] = token
        }
    }

    suspend fun clearApiToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(apiTokenKey)
        }
    }

    suspend fun getModel(): String = context.dataStore.data
        .map { preferences -> preferences[modelKey] ?: "anthropic/claude-sonnet-4.5" }
        .first()

    suspend fun setModel(model: String) {
        context.dataStore.edit { preferences ->
            preferences[modelKey] = model
        }
    }

    suspend fun getDarkMode(): Boolean = context.dataStore.data
        .map { preferences -> preferences[darkModeKey] ?: true }
        .first()

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[darkModeKey] = enabled
        }
    }

    suspend fun getNotificationsEnabled(): Boolean = context.dataStore.data
        .map { preferences -> preferences[notificationsKey] ?: true }
        .first()

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsKey] = enabled
        }
    }

    suspend fun getAutoSaveSessions(): Boolean = context.dataStore.data
        .map { preferences -> preferences[autoSaveKey] ?: true }
        .first()

    suspend fun setAutoSaveSessions(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[autoSaveKey] = enabled
        }
    }
}
