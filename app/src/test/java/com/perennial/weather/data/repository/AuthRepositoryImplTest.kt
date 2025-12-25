package com.perennial.weather.data.repository

import org.junit.Assert.*
import com.perennial.weather.data.local.dao.UserDao
import com.perennial.weather.data.local.entity.UserEntity
import com.perennial.weather.utils.PasswordUtils
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var userDao: UserDao

    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setup() {
        authRepository = AuthRepositoryImpl(userDao)
    }

    @Test
    fun `register should return true when user does not exist`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        whenever(userDao.getUserByEmail(email)).thenReturn(null)

        val result = authRepository.register(name, email, password)

        assertTrue(result)
        verify(userDao).getUserByEmail(email)
        verify(userDao).registerUser(any())
    }

    @Test
    fun `register should return false when user already exists`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val existingUser = UserEntity(1, "Test", email, "hashedPassword")
        whenever(userDao.getUserByEmail(email)).thenReturn(existingUser)

        val result = authRepository.register(name, email, password)

        assertFalse(result)
        verify(userDao).getUserByEmail(email)
        verify(userDao, org.mockito.kotlin.never()).registerUser(any())
    }

    @Test
    fun `register should hash password before saving`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val expectedHashedPassword = PasswordUtils.hashPassword(password)
        whenever(userDao.getUserByEmail(email)).thenReturn(null)

        authRepository.register(name, email, password)

        verify(userDao).registerUser(
            org.mockito.kotlin.argThat { user ->
                user.email == email && user.name == name && user.password == expectedHashedPassword
            }
        )
    }

    @Test
    fun `login should return true when credentials are correct`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val hashedPassword = PasswordUtils.hashPassword(password)
        val user = UserEntity(1, "Test", email, hashedPassword)
        whenever(userDao.login(email, hashedPassword)).thenReturn(user)

        val result = authRepository.login(email, password)

        assertTrue(result)
        verify(userDao).login(email, hashedPassword)
    }

    @Test
    fun `login should return false when credentials are incorrect`() = runTest {
        val email = "test@example.com"
        val password = "wrongPassword"
        val hashedPassword = PasswordUtils.hashPassword(password)
        whenever(userDao.login(email, hashedPassword)).thenReturn(null)

        val result = authRepository.login(email, password)

        assertFalse(result)
        verify(userDao).login(email, hashedPassword)
    }

    @Test
    fun `login should hash password before checking`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val expectedHashedPassword = PasswordUtils.hashPassword(password)
        whenever(userDao.login(email, expectedHashedPassword)).thenReturn(null)

        authRepository.login(email, password)

        verify(userDao).login(email, expectedHashedPassword)
    }
}

