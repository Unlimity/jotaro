[jotaro](../../index.md) / [com.unlimity.jotaro](../index.md) / [Preferences](./index.md)

# Preferences

`open class Preferences`

### Types

| Name | Summary |
|---|---|
| [Preference](-preference/index.md) | `inner class Preference<R, T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> : `[`ReadWriteProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-write-property/index.html)`<`[`R`](-preference/index.md#R)`, `[`T`](-preference/index.md#T)`>` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Preferences(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = Context.MODE_PRIVATE, isApplyOnSet: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true)`<br>`Preferences(preferences: `[`SharedPreferences`](https://developer.android.com/reference/android/content/SharedPreferences.html)`, isApplyOnSet: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true)` |

### Properties

| Name | Summary |
|---|---|
| [preferences](preferences.md) | `val preferences: `[`SharedPreferences`](https://developer.android.com/reference/android/content/SharedPreferences.html) |
