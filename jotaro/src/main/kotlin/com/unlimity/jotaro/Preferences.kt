package com.unlimity.jotaro

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Preferences(
    private val preferences: SharedPreferences
) {
    constructor(context: Context, name: String) : this(context.getSharedPreferences(name, Context.MODE_PRIVATE))

    inner class Preference<R, T>(
        private val name: String,
        private val default: T,
        private val serializer: (T) -> String = { it.toString() },
        private val deserializer: (String) -> T = { it as T }
    ) : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>): T {
            return when(default) {
                is Int -> preferences.getInt(name, default) as T
                is Long -> preferences.getLong(name, default) as T
                is Float -> preferences.getFloat(name, default) as T
                is Boolean -> preferences.getBoolean(name, default) as T
                is String -> preferences.getString(name, default) as T
                is Set<*> -> preferences.getStringSet(name, default as Set<String>) as T
                else -> {
                    val string = preferences.getString(name, null)
                    return string?.let(deserializer) ?: default
                }
            }
        }

        override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            val editor = preferences.edit()

            when(value) {
                is Int -> editor.putInt(name, value)
                is Long -> editor.putLong(name, value)
                is Float -> editor.putFloat(name, value)
                is Boolean -> editor.putBoolean(name, value)
                is String -> editor.putString(name, value)
                is Set<*> -> editor.putStringSet(name, value as Set<String>)
                else -> editor.putString(name, serializer(value))
            }

            editor.apply()
        }
    }
}
