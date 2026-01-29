package com.example.askanyone.datastore

//Data is stored in a file

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
///data/data/your.package.name/files/datastore/auth_prefs.preferences_pb is where it is stored
private val Context.dataStore by preferencesDataStore(name = "auth_prefs") //creates a datastore instance"datastore" and tie it to context

class TokenManager(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("jwt_token") //defines a typed key named jwt_token of type string
    //TOKEN_KEY is the typed key to access the value(just a variable for the key value pair)
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }
//context.dataStore.data is like a flow , we need first to take the first one
    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[TOKEN_KEY]
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)// edit (operation) we need lambda for the operations
            // {} is used to define a function and here we do it
        }
    }
}
