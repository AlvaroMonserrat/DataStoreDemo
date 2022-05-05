package com.rrat.datastoredemo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPrefs(private val context: Context) {

    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        private val idVendedorKey = intPreferencesKey("id_vendedor")
    }

    suspend fun saveIdVendedor(idVendedor: Int){
        context.dataStore.edit { it[idVendedorKey] = idVendedor }
    }

    val getIdVendedor: Flow<Int> get() = context.dataStore.data.map { it[idVendedorKey] ?: 0}
}