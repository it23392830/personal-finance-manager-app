package com.example.financeflow.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Create DataStore instance using delegation on Context
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

/**
 * PreferencesManager handles persistent preferences for the user,
 * specifically the "Remember Me" toggle status.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val rememberMeKey = booleanPreferencesKey("remember_me")

    // Flow that emits changes to the Remember Me boolean preference
    val rememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[rememberMeKey] ?: false
        }

    /**
     * Saves the Remember Me preference status.
     */
    suspend fun setRememberMe(remember: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[rememberMeKey] = remember
        }
    }

    /**
     * Clears all saved preferences in DataStore.
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
