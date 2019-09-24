# Jotaro
[![Bintray version](https://api.bintray.com/packages/alviere/maven/jotaro/images/download.svg)](https://bintray.com/alviere/maven/jotaro)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3.50-blue.svg)](http://kotlinlang.org/)

Kotlin way to store and retrieve `SharedPreferences` in Android

### How to use

The approach of this library is to provide access to `SharedPreferences` through class with delegated properties and
allows you to interact with your preferences just like with regular Kotlin properties:

```kotlin
class MyPreferences(context: Context) : Preferences(context, "MyPreferences") {
    var myInt by Preference("myInt", 0)
    var myString by Preference("myString", "default")
}

fun foo(context: Context) {
    val preferences = MyPreferences(context)
    
    preferences.myInt = 50 // Saved to SharedPreferences with apply()
    val string = preferences.myString // "default" returned since there is no value provided yet
}
```

In case there is need for abstraction, you can always hide your preferences behind interface:

```kotlin
interface MyPreferences {
    var myInt: Int
    var myString: String
}

class MyPreferencesImpl(context: Context) : Preferences(context, "MyPreferences"), MyPreferences {
    override var myInt by Preference("myInt", 0)
    override var myString by Preference("myString", "default")
}
```

#### Coroutines

If you need to acquire your preferences in asynchronous way, library provides 2 extension functions to
provide lazy-initialized `Deferred<T>` tasks:

```kotlin
val preferences = MyPreferences(context)

fun deferredInsideScope() {
   MainScope().launch {
        val text = asDeferred(preferences::myString).await()
        myTextView.text = text
    }
}

fun deferredOutsideScope(scope: CoroutineScope) {
    val deferred = preferences::myString.asDeferred(scope)
    
    MainScope().launch {
        val text = deferred.await()
        myTextView.text = text
    }
}
```

#### Custom types

By default, library supports all types that `SharedPreferences` support out of the box:
 - Int
 - Long
 - Float
 - Boolean
 - String
 - Set<String> (it will treat any Set<*> as Set<String> due to the type erasure)
 
But if you need to store some custom objects in the preferences, library has you covered:

```kotlin
data class User(val id: Int, val name: String)

class MyPreferences(context: Context) : Preferences(context, "MyPreferences") {
    var user by Preference(
        "user",
        User(0, ""),
        serializer = { user -> "${user.id}|${user.name}" },
        deserializer = { string -> string.split('|').let { User(it[0].toInt(), it[1]) } }
    )
}
```

`serializer` and `deserializer` arguments are being invoked when the type of the preference is not supported
out of the box. You can write manual serialization, or use any third party libraries, such as `Gson`.

### Install

Maven
```xml
<dependency>
  <groupId>com.unlimity.jotaro</groupId>
  <artifactId>jotaro</artifactId>
  <version>LATEST_VERSION</version>
  <type>pom</type>
</dependency>
```
or Gradle:
```groovy
repositories {
    jcenter()
}

dependencies {
    implementation 'com.unlimity.jotaro:jotaro:LATEST_VERSION'
}
```

### License

Jotaro is available under the [Apache License, Version 2.0](https://github.com/unlimity/jotaro/blob/master/LICENSE).
