package com.unlimity.jotaro.gson

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class GsonPreferences(
    private val gson: Gson,
    private val preferences: SharedPreferences
) {
    constructor(gson: Gson, context: Context, name: String, mode: Int = Context.MODE_PRIVATE)
            : this(gson, context.getSharedPreferences(name, mode))

    inner class Preference<R, T : Any>(
        private val name: String,
        private val default: T
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
                    return string?.let { gson.fromJson<T>(it, default::class.java) } ?: default
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
                else -> editor.putString(name, gson.toJson(value))
            }

            editor.apply()
        }
    }
}