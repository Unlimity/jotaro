package com.unlimity.jotaro.sample

import android.content.SharedPreferences
import com.unlimity.jotaro.Preferences

class JotaroPreferences(preferences: SharedPreferences) : Preferences(preferences) {
    var int: Int by Preference("int", 0)
    var long: Long by Preference("long", 0L)
    var float: Float by Preference("float", .0f)
    var boolean: Boolean by Preference("boolean", false)
    var string: String by Preference("string", "")
    var custom: Custom by Preference(
        name = "custom",
        default = Custom(0, ""),
        serializer = { "${it.id}|${it.name}" },
        deserializer = { it.split('|').let { list -> Custom(list[0].toInt(), list[1]) } }
    )

    data class Custom(val id: Int, val name: String)
}
