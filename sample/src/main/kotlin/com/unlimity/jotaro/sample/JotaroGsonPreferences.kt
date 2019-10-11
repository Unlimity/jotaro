package com.unlimity.jotaro.sample

import android.content.SharedPreferences
import com.google.gson.Gson
import com.unlimity.jotaro.gson.GsonPreferences

class JotaroGsonPreferences(preferences: SharedPreferences) : GsonPreferences(preferences, Gson()) {
    var custom: Custom by Preference(
        name = "custom",
        default = Custom(0, "", 160.5f)
    )

    data class Custom(val id: Int, val name: String, val height: Float)
}
