package com.bouledepate.caffy.core.storage

import android.content.Context
import java.util.*

object Storage {
    private val STORAGE_NAME = "APP_PREFERENCES"

    fun getString(key: StorageKeys, context: Context): String {
        val sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key.toString(), "").toString();
    }

    fun getBoolean(key: StorageKeys, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key.toString(), true);
    }

    fun getInt(key: StorageKeys, context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key.toString(), -1);
    }

    fun putString(key: StorageKeys, value: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key.toString(), value).apply()
    }

    fun putBoolean(key: StorageKeys, value: Boolean, context: Context) {
        val sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(key.toString(), value).apply()
    }

    fun putInt(key: StorageKeys, value: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(key.toString(), value).apply()
    }
}