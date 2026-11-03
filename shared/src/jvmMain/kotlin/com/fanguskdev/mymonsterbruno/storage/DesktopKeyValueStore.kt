package com.fanguskdev.mymonsterbruno.storage

import java.util.prefs.Preferences

/**
 * What the game remembers on the desktop, in the user's Java preferences. Every write goes to disk at once: a desktop
 * window can be closed in ways that skip the preferences' own sync.
 */
class DesktopKeyValueStore : KeyValueStore {
    private val preferences = Preferences.userRoot().node("com/fanguskdev/mymonsterbruno")

    override fun getString(key: String): String? = preferences.get(key, null)

    override fun putString(key: String, value: String) {
        preferences.put(key, value)
        preferences.flush()
    }

    override fun remove(key: String) {
        preferences.remove(key)
        preferences.flush()
    }
}
