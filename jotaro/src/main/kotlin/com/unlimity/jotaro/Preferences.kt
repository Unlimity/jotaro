package com.unlimity.jotaro

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
open class Preferences(
    protected val preferences: SharedPreferences,
    private val isApplyOnSet: Boolean = true
) {
    constructor(context: Context, name: String, mode: Int = Context.MODE_PRIVATE, isApplyOnSet: Boolean = true)
            : this(context.getSharedPreferences(name, mode), isApplyOnSet)

    inner class Preference<R, T : Any>(
        private val name: String,
        private val default: T,
        private val isApplyOnSet: Boolean = true,
        private val serializer: (T) -> String = { it.toString() },
        private val deserializer: (String) -> T = { it as T }
    ) : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>): T {
            return with(preferences) {
                when(default) {
                    is Int -> getInt(name, default) as T
                    is Long -> getLong(name, default) as T
                    is Float -> getFloat(name, default) as T
                    is Boolean -> getBoolean(name, default) as T
                    is String -> getString(name, default) as T
                    is Set<*> -> getStringSet(name, default as Set<String>) as T
                    else -> {
                        val string = getString(name, null)
                        return string?.let(deserializer) ?: default
                    }
                }
            }
        }

        @SuppressLint("ApplySharedPref")
        override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            with(preferences.edit()) {
                when (value) {
                    is Int -> putInt(name, value)
                    is Long -> putLong(name, value)
                    is Float -> putFloat(name, value)
                    is Boolean -> putBoolean(name, value)
                    is String -> putString(name, value)
                    is Set<*> -> putStringSet(name, value as Set<String>)
                    else -> putString(name, serializer(value))
                }

                if (!this@Preferences.isApplyOnSet || !isApplyOnSet) {
                    commit()
                } else {
                    apply()
                }
            }
        }
    }
}
