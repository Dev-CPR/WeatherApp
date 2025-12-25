package com.perennial.weather.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
class UserPreferencesManagerTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreferencesManager: UserPreferencesManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        userPreferencesManager = UserPreferencesManager(sharedPreferences)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun testConstructor() {
        val testPrefs = context.getSharedPreferences("test1", Context.MODE_PRIVATE)
        val manager = UserPreferencesManager(testPrefs)
        assertNotNull(manager)
    }

    @Test
    fun testConstructorStoresSharedPreferences() {
        val testPrefs = context.getSharedPreferences("test2", Context.MODE_PRIVATE)
        val manager = UserPreferencesManager(testPrefs)
        manager.saveUserEmail("test@example.com")
        assertEquals("test@example.com", testPrefs.getString("user_email", null))
    }

    @Test
    fun testSaveUserEmail_SavesEmail() {
        val email = "test@example.com"
        userPreferencesManager.saveUserEmail(email)
        assertEquals(email, sharedPreferences.getString("user_email", null))
    }

    @Test
    fun testSaveUserEmail_OverwritesExisting() {
        userPreferencesManager.saveUserEmail("first@test.com")
        userPreferencesManager.saveUserEmail("second@test.com")
        assertEquals("second@test.com", userPreferencesManager.getUserEmail())
    }

    @Test
    fun testSaveUserEmail_HandlesEmptyString() {
        userPreferencesManager.saveUserEmail("")
        assertEquals("", userPreferencesManager.getUserEmail())
    }

    @Test
    fun testSaveUserEmail_HandlesSpecialCharacters() {
        val email = "test.user+tag@example.com"
        userPreferencesManager.saveUserEmail(email)
        assertEquals(email, userPreferencesManager.getUserEmail())
    }

    @Test
    fun testSaveUserEmail_ExecutesAllLines() {
        val email = "alllines@test.com"
        userPreferencesManager.saveUserEmail(email)
        assertTrue(sharedPreferences.contains("user_email"))
        assertEquals(email, sharedPreferences.getString("user_email", null))
    }

    @Test
    fun testGetUserEmail_ReturnsNullWhenNoEmail() {
        val result = userPreferencesManager.getUserEmail()
        assertNull(result)
    }

    @Test
    fun testGetUserEmail_ReturnsSavedEmail() {
        val email = "test@example.com"
        userPreferencesManager.saveUserEmail(email)
        val result = userPreferencesManager.getUserEmail()
        assertEquals(email, result)
    }

    @Test
    fun testGetUserEmail_ReturnsNullAfterClear() {
        userPreferencesManager.saveUserEmail("test@example.com")
        userPreferencesManager.clearUserEmail()
        assertNull(userPreferencesManager.getUserEmail())
    }

    @Test
    fun testGetUserEmail_ExecutesAllLines() {
        val email = "alllines@test.com"
        userPreferencesManager.saveUserEmail(email)
        val result = userPreferencesManager.getUserEmail()
        assertEquals(email, result)
    }

    @Test
    fun testGetUserEmail_ExecutesAllLinesWithNull() {
        val result = userPreferencesManager.getUserEmail()
        assertNull(result)
    }

    @Test
    fun testClearUserEmail_RemovesEmail() {
        userPreferencesManager.saveUserEmail("test@example.com")
        userPreferencesManager.clearUserEmail()
        assertFalse(sharedPreferences.contains("user_email"))
        assertNull(userPreferencesManager.getUserEmail())
    }

    @Test
    fun testClearUserEmail_WhenKeyDoesNotExist() {
        sharedPreferences.edit().remove("user_email").apply()
        userPreferencesManager.clearUserEmail()
        assertFalse(sharedPreferences.contains("user_email"))
    }

    @Test
    fun testClearUserEmail_OnlyRemovesUserEmailKey() {
        sharedPreferences.edit()
            .putString("user_email", "test@example.com")
            .putString("other_key", "other_value")
            .apply()
        userPreferencesManager.clearUserEmail()
        assertFalse(sharedPreferences.contains("user_email"))
        assertTrue(sharedPreferences.contains("other_key"))
    }

    @Test
    fun testClearUserEmail_ExecutesAllLines() {
        userPreferencesManager.saveUserEmail("alllines@test.com")
        userPreferencesManager.clearUserEmail()
        assertFalse(sharedPreferences.contains("user_email"))
    }

    @Test
    fun testAllMethodsInSequence() {
        val manager = UserPreferencesManager(sharedPreferences)
        manager.saveUserEmail("seq1@test.com")
        assertEquals("seq1@test.com", manager.getUserEmail())
        manager.clearUserEmail()
        assertNull(manager.getUserEmail())
        manager.saveUserEmail("seq2@test.com")
        assertEquals("seq2@test.com", manager.getUserEmail())
    }

    @Test
    fun testCompleteWorkflow() {
        val manager = UserPreferencesManager(sharedPreferences)
        manager.saveUserEmail("workflow@test.com")
        assertEquals("workflow@test.com", manager.getUserEmail())
        manager.clearUserEmail()
        assertNull(manager.getUserEmail())
    }

    @Test
    fun testEverySingleLineExecution() {
        val manager = UserPreferencesManager(sharedPreferences)
        
        manager.saveUserEmail("everyline@test.com")
        assertEquals("everyline@test.com", manager.getUserEmail())
        manager.clearUserEmail()
        assertNull(manager.getUserEmail())

        manager.saveUserEmail("everyline2@test.com")
        assertEquals("everyline2@test.com", manager.getUserEmail())
        manager.clearUserEmail()
        assertNull(manager.getUserEmail())
    }

    @Test
    fun testPropertyKeyUserEmailInAllMethods() {
        val email = "property@test.com"

        userPreferencesManager.saveUserEmail(email)
        assertTrue(sharedPreferences.contains("user_email"))

        assertEquals(email, userPreferencesManager.getUserEmail())

        userPreferencesManager.clearUserEmail()
        assertFalse(sharedPreferences.contains("user_email"))
    }
}
