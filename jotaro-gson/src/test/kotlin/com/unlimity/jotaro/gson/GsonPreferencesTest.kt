package com.unlimity.jotaro.gson

import android.content.SharedPreferences
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import com.unlimity.jotaro.asDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GsonPreferencesTest {
    @Mock
    lateinit var shared: SharedPreferences

    @Mock
    lateinit var editor: SharedPreferences.Editor

    private lateinit var preferences: TestPreferences

    @Before
    fun setup() {
        preferences = TestPreferences(shared)
    }

    @Test
    fun testInt() {
        // Arrange
        var storage = 0

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putInt(eq("int"), any())).then { storage = it.arguments[1] as Int; editor }
        whenever(shared.getInt(eq("int"), any())).then { storage }

        // Act
        preferences.int = 50
        val result = preferences.int

        // Assert
        assert(result == 50)
    }

    @Test
    fun testLong() {
        // Arrange
        var storage = 0L

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putLong(eq("long"), any())).then { storage = it.arguments[1] as Long; editor }
        whenever(shared.getLong(eq("long"), any())).then { storage }

        // Act
        preferences.long = 50L
        val result = preferences.long

        // Assert
        assert(result == 50L)
    }

    @Test
    fun testFloat() {
        // Arrange
        var storage = .0f

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putFloat(eq("float"), any())).then { storage = it.arguments[1] as Float; editor }
        whenever(shared.getFloat(eq("float"), any())).then { storage }

        // Act
        preferences.float = .5f
        val result = preferences.float

        // Assert
        assert(result == .5f)
    }

    @Test
    fun testBoolean() {
        // Arrange
        var storage = false

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putBoolean(eq("boolean"), any())).then { storage = it.arguments[1] as Boolean; editor }
        whenever(shared.getBoolean(eq("boolean"), any())).then { storage }

        // Act
        preferences.boolean = true
        val result = preferences.boolean

        // Assert
        assert(result)
    }

    @Test
    fun testString() {
        // Arrange
        var storage = ""

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putString(eq("string"), any())).then { storage = it.arguments[1] as String; editor }
        whenever(shared.getString(eq("string"), any())).then { storage }

        // Act
        preferences.string = "test"
        val result = preferences.string

        // Assert
        assert(result == "test")
    }

    @Test
    fun testStringSet() {
        // Arrange
        var storage = setOf<String>()

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putStringSet(eq("set"), any())).then { storage = it.arguments[1] as Set<String>; editor }
        whenever(shared.getStringSet(eq("set"), any())).then { storage }

        // Act
        preferences.set = setOf("A", "B", "C")
        val result = preferences.set

        // Assert
        assert(result == setOf("A", "B", "C"))
    }

    @Test
    fun testCustom() {
        // Arrange
        var storage = ""

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putString(eq("custom"), any())).then { storage = it.arguments[1] as String; editor }
        whenever(shared.getString(eq("custom"), eq(null))).then { storage }

        // Act
        preferences.custom = Custom(50, "test")
        val result = preferences.custom

        // Assert
        assert(result == Custom(50, "test"))
    }

    @Test
    fun testAsDeferredInScope() = runBlocking {
        // Arrange
        var storage = 0

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putInt(eq("int"), any())).then { storage = it.arguments[1] as Int; editor }
        whenever(shared.getInt(eq("int"), any())).then { storage }

        // Act
        preferences.int = 50
        val result = asDeferred(preferences::int).await()

        // Assert
        assert(result == 50)

        // Act
        asDeferred(preferences::int, 55). await()

        // Assert
        assert(storage == 55)
    }

    @Test
    fun testAsDeferredInProperty() = runBlocking {
        // Arrange
        var storage = 0

        whenever(shared.edit()).thenReturn(editor)
        whenever(editor.putInt(eq("int"), any())).then { storage = it.arguments[1] as Int; editor }
        whenever(shared.getInt(eq("int"), any())).then { storage }

        // Act
        preferences.int = 50
        val result = preferences::int.asDeferred(this).await()

        // Assert
        assert(result == 50)

        // Act
        preferences::int.asDeferred(this, 55).await()

        // Assert
        assert(storage == 55)
    }


    @Test
    fun testCommitOnSinglePreference() {
        // Arrange
        val prefs = TestCommitPreferences(shared)

        whenever(shared.edit()).thenReturn(editor)

        // Act
        prefs.int = 50

        // Assert
        verify(editor).apply()
        verify(editor, times(0)).commit()

        // Act
        prefs.long = 50L

        // Assert
        verify(editor).commit()
    }

    @Test
    fun testCommitOnClassPreference() {
        // Arrange
        val prefs = TestCommitAllPreferences(shared)

        whenever(shared.edit()).thenReturn(editor)

        // Act
        prefs.int = 50
        prefs.long = 50L

        // Assert
        verify(editor, times(0)).apply()
        verify(editor, times(2)).commit()
    }

    class TestPreferences(shared: SharedPreferences) : GsonPreferences(Gson(), shared) {
        var int by Preference("int", 0)
        var long by Preference("long", 0L)
        var float by Preference("float", .0f)
        var boolean by Preference("boolean", false)
        var string by Preference("string", "")
        var set by Preference("set", setOf<String>())
        var custom by Preference("custom", Custom(0, ""))
    }


    class TestCommitPreferences(shared: SharedPreferences) : GsonPreferences(Gson(), shared) {
        var int by Preference("int", 0, true)
        var long by Preference("long", 0L, false)
    }

    class TestCommitAllPreferences(shared: SharedPreferences) : GsonPreferences(Gson(), shared, false) {
        var int by Preference("int", 0, true)
        var long by Preference("long", 0L, false)
    }

    data class Custom(val id: Int, val name: String)
}