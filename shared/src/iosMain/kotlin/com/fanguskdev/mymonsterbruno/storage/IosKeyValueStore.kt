package com.fanguskdev.mymonsterbruno.storage

import platform.Foundation.NSUserDefaults

/** What the game remembers, in the app's NSUserDefaults. */
class IosKeyValueStore : KeyValueStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getString(key: String): String? = defaults.stringForKey(key)

    override fun putString(key: String, value: String) = defaults.setObject(value, key)

    override fun remove(key: String) = defaults.removeObjectForKey(key)
}
