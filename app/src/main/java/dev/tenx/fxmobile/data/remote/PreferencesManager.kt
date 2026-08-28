package dev.tenx.fxmobile.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
        .map { preferences -> preferences[modelKey] ?: "kimi-k2" }
        .first()

    suspend fun setModel(model: String) {
        context.dataStore.edit { preferences ->
            preferences[modelKey] = model
        }
    }
}
