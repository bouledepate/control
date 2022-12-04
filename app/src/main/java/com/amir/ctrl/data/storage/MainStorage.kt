package com.bouledepate.caffy.core.storage

import android.content.Context
import java.util.*

object MainStorage {
    private val storage = Storage;

    fun email(context: Context): String {
        return storage.getString(StorageKeys.USER_EMAIL, context)
    }

    fun password(context: Context): String {
        return storage.getString(StorageKeys.USER_PASSWORD, context)
    }

    fun email(context: Context, value: String) {
        storage.putString(StorageKeys.USER_EMAIL, value, context)
    }

    fun password(context: Context, value: String) {
        storage.putString(StorageKeys.USER_PASSWORD, value, context)
    }
}