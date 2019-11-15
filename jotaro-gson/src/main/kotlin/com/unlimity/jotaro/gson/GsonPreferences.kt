package com.unlimity.jotaro.gson

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
open class GsonPreferences(
    private val gson: Gson,
    private val preferences: SharedPreferences,
    private val isApplyOnSet: Boolean = true
) {
    constructor(gson: Gson, context: Context, name: String, mode: Int = Context.MODE_PRIVATE, isApplyOnSet: Boolean = true)
            : this(gson, context.getSharedPreferences(name, mode), isApplyOnSet)

    inner class Preference<R, T : Any>(
        private val name: String,
        private val default: T,
        private val isApplyOnSet: Boolean = true
    ) : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>): T {
            return with(preferences) {
                when (default) {
                    is Int -> getInt(name, default) as T
                    is Long -> getLong(name, default) as T
                    is Float -> getFloat(name, default) as T
                    is Boolean -> getBoolean(name, default) as T
                    is String -> getString(name, default) as T
                    is Set<*> -> getStringSet(name, default as Set<String>) as T
                    else -> {
                        val string = getString(name, null)
                        return string?.let { gson.fromJson<T>(it, default::class.java) } ?: default
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
                    else -> putString(name, gson.toJson(value))
                }

                if (!this@GsonPreferences.isApplyOnSet || !isApplyOnSet) {
                    commit()
                } else {
                    apply()
                }
            }
        }
    }
}