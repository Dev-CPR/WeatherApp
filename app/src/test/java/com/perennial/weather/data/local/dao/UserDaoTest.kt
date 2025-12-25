package com.perennial.weather.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.perennial.weather.data.local.database.WeatherDatabase
import com.perennial.weather.data.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
class UserDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun registerUser_shouldInsertUserSuccessfully() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val retrievedUser = userDao.getUserByEmail("test@example.com")
        assertNotNull(retrievedUser)
        assertEquals("Test User", retrievedUser?.name)
        assertEquals("test@example.com", retrievedUser?.email)
        assertEquals("hashedPassword123", retrievedUser?.password)
    }

    @Test
    fun registerUser_shouldGenerateAutoIncrementId() = runTest {
        val user1 = UserEntity(
            name = "User 1",
            email = "user1@example.com",
            password = "password1"
        )
        val user2 = UserEntity(
            name = "User 2",
            email = "user2@example.com",
            password = "password2"
        )

        userDao.registerUser(user1)
        userDao.registerUser(user2)

        val retrievedUser1 = userDao.getUserByEmail("user1@example.com")
        val retrievedUser2 = userDao.getUserByEmail("user2@example.com")

        assertNotNull(retrievedUser1)
        assertNotNull(retrievedUser2)
        assertTrue(retrievedUser1!!.id > 0)
        assertTrue(retrievedUser2!!.id > retrievedUser1.id)
    }

    @Test
    fun registerUser_shouldAllowDuplicateEmailsAtDaoLevel() = runTest {
        val user1 = UserEntity(
            name = "User 1",
            email = "duplicate@example.com",
            password = "password1"
        )
        val user2 = UserEntity(
            name = "User 2",
            email = "duplicate@example.com",
            password = "password2"
        )

        userDao.registerUser(user1)
        userDao.registerUser(user2)

        val retrievedUser1 = userDao.login("duplicate@example.com", "password1")
        val retrievedUser2 = userDao.login("duplicate@example.com", "password2")
        
        assertNotNull(retrievedUser1)
        assertNotNull(retrievedUser2)
        assertEquals("User 1", retrievedUser1?.name)
        assertEquals("User 2", retrievedUser2?.name)
        assertTrue(retrievedUser1!!.id != retrievedUser2!!.id)
    }

    @Test
    fun getUserByEmail_shouldReturnUserWhenEmailExists() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val retrievedUser = userDao.getUserByEmail("test@example.com")
        assertNotNull(retrievedUser)
        assertEquals("Test User", retrievedUser?.name)
        assertEquals("test@example.com", retrievedUser?.email)
        assertEquals("hashedPassword123", retrievedUser?.password)
    }

    @Test
    fun getUserByEmail_shouldReturnNullWhenEmailDoesNotExist() = runTest {
        val retrievedUser = userDao.getUserByEmail("nonexistent@example.com")
        assertNull(retrievedUser)
    }

    @Test
    fun getUserByEmail_shouldBeCaseSensitiveForEmail() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val retrievedUser = userDao.getUserByEmail("TEST@EXAMPLE.COM")
        assertNull(retrievedUser)
    }

    @Test
    fun getUserByEmail_shouldReturnCorrectUserWithSpecialCharactersInEmail() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test.user+tag@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val retrievedUser = userDao.getUserByEmail("test.user+tag@example.com")
        assertNotNull(retrievedUser)
        assertEquals("test.user+tag@example.com", retrievedUser?.email)
    }

    @Test
    fun login_shouldReturnUserWhenEmailAndPasswordMatch() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val loggedInUser = userDao.login("test@example.com", "hashedPassword123")
        assertNotNull(loggedInUser)
        assertEquals("Test User", loggedInUser?.name)
        assertEquals("test@example.com", loggedInUser?.email)
        assertEquals("hashedPassword123", loggedInUser?.password)
    }

    @Test
    fun login_shouldReturnNullWhenEmailDoesNotExist() = runTest {
        val loggedInUser = userDao.login("nonexistent@example.com", "password123")
        assertNull(loggedInUser)
    }

    @Test
    fun login_shouldReturnNullWhenPasswordDoesNotMatch() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val loggedInUser = userDao.login("test@example.com", "wrongPassword")
        assertNull(loggedInUser)
    }

    @Test
    fun login_shouldReturnNullWhenBothEmailAndPasswordDoNotMatch() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val loggedInUser = userDao.login("wrong@example.com", "wrongPassword")
        assertNull(loggedInUser)
    }

    @Test
    fun login_shouldBeCaseSensitiveForEmail() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val loggedInUser = userDao.login("TEST@EXAMPLE.COM", "hashedPassword123")
        assertNull(loggedInUser)
    }

    @Test
    fun login_shouldBeCaseSensitiveForPassword() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val loggedInUser = userDao.login("test@example.com", "HASHEDPASSWORD123")
        assertNull(loggedInUser)
    }

    @Test
    fun multipleUsers_canBeRegisteredWithDifferentEmails() = runTest {
        val user1 = UserEntity(
            name = "User 1",
            email = "user1@example.com",
            password = "password1"
        )
        val user2 = UserEntity(
            name = "User 2",
            email = "user2@example.com",
            password = "password2"
        )
        val user3 = UserEntity(
            name = "User 3",
            email = "user3@example.com",
            password = "password3"
        )

        userDao.registerUser(user1)
        userDao.registerUser(user2)
        userDao.registerUser(user3)

        val retrievedUser1 = userDao.getUserByEmail("user1@example.com")
        val retrievedUser2 = userDao.getUserByEmail("user2@example.com")
        val retrievedUser3 = userDao.getUserByEmail("user3@example.com")

        assertNotNull(retrievedUser1)
        assertNotNull(retrievedUser2)
        assertNotNull(retrievedUser3)
        assertEquals("User 1", retrievedUser1?.name)
        assertEquals("User 2", retrievedUser2?.name)
        assertEquals("User 3", retrievedUser3?.name)
    }

    @Test
    fun login_shouldWorkCorrectlyForMultipleUsers() = runTest {
        val user1 = UserEntity(
            name = "User 1",
            email = "user1@example.com",
            password = "password1"
        )
        val user2 = UserEntity(
            name = "User 2",
            email = "user2@example.com",
            password = "password2"
        )

        userDao.registerUser(user1)
        userDao.registerUser(user2)

        val loggedInUser1 = userDao.login("user1@example.com", "password1")
        val loggedInUser2 = userDao.login("user2@example.com", "password2")

        assertNotNull(loggedInUser1)
        assertNotNull(loggedInUser2)
        assertEquals("User 1", loggedInUser1?.name)
        assertEquals("User 2", loggedInUser2?.name)
    }

    @Test
    fun getUserByEmail_shouldReturnNullForEmptyEmail() = runTest {
        val retrievedUser = userDao.getUserByEmail("")
        assertNull(retrievedUser)
    }

    @Test
    fun login_shouldReturnNullForEmptyEmail() = runTest {
        val loggedInUser = userDao.login("", "password")
        assertNull(loggedInUser)
    }

    @Test
    fun login_shouldReturnNullForEmptyPassword() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test@example.com",
            password = "hashedPassword123"
        )

        userDao.registerUser(user)

        val loggedInUser = userDao.login("test@example.com", "")
        assertNull(loggedInUser)
    }

    @Test
    fun registerUser_shouldPreserveAllUserFields() = runTest {
        val user = UserEntity(
            name = "Test User",
            email = "test.user@example.com",
            password = "securePassword123!@#"
        )

        userDao.registerUser(user)

        val retrievedUser = userDao.getUserByEmail("test.user@example.com")
        assertNotNull(retrievedUser)
        assertEquals("Test User", retrievedUser?.name)
        assertEquals("test.user@example.com", retrievedUser?.email)
        assertEquals("securePassword123!@#", retrievedUser?.password)
        assertTrue(retrievedUser!!.id > 0)
    }

    @Test
    fun getUserByEmail_shouldReturnCorrectUserAfterMultipleRegistrations() = runTest {
        val user1 = UserEntity(
            name = "First User",
            email = "first@example.com",
            password = "password1"
        )
        val user2 = UserEntity(
            name = "Second User",
            email = "second@example.com",
            password = "password2"
        )

        userDao.registerUser(user1)
        userDao.registerUser(user2)

        val retrievedUser1 = userDao.getUserByEmail("first@example.com")
        val retrievedUser2 = userDao.getUserByEmail("second@example.com")

        assertNotNull(retrievedUser1)
        assertNotNull(retrievedUser2)
        assertEquals("First User", retrievedUser1?.name)
        assertEquals("Second User", retrievedUser2?.name)
        assertNotEquals(retrievedUser1?.id, retrievedUser2?.id)
    }
}

