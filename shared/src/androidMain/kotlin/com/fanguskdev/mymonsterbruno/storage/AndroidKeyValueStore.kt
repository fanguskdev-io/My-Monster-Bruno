package com.fanguskdev.mymonsterbruno.storage

import android.content.Context

/** What the game remembers, in the app's SharedPreferences. */
class AndroidKeyValueStore(context: Context) : KeyValueStore {
    private val preferences = context.getSharedPreferences("mymonsterbruno", Context.MODE_PRIVATE)

    override fun getString(key: String): String? = preferences.getString(key, null)

    override fun putString(key: String, value: String) = preferences.edit().putString(key, value).apply()

    override fun remove(key: String) = preferences.edit().remove(key).apply()
}
