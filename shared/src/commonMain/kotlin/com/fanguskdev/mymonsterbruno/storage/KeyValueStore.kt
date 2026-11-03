package com.fanguskdev.mymonsterbruno.storage

/**
 * The little the game remembers between launches, kept on the device by each platform: SharedPreferences on Android,
 * NSUserDefaults on iOS, Preferences on the desktop. Nothing leaves the device (ADR 0005).
 */
interface KeyValueStore {
    fun getString(key: String): String?

    fun putString(key: String, value: String)

    fun remove(key: String)
}

/** A store that forgets everything when the app closes: for the tests and the screenshots. */
class MemoryStore : KeyValueStore {
    private val values = HashMap<String, String>()

    override fun getString(key: String): String? = values[key]

    override fun putString(key: String, value: String) {
        values[key] = value
    }

    override fun remove(key: String) {
        values.remove(key)
    }
}
