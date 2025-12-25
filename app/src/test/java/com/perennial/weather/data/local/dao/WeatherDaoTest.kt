package com.perennial.weather.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.perennial.weather.data.local.database.WeatherDatabase
import com.perennial.weather.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWeather_shouldInsertWeatherSuccessfully() = runTest {
        val weather = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val retrievedWeather = weatherDao.getLatestWeather()
        assertNotNull(retrievedWeather)
        assertEquals("New York", retrievedWeather?.city)
        assertEquals("test@example.com", retrievedWeather?.userEmail)
    }

    @Test
    fun insertWeather_shouldPreserveAllWeatherEntityFields() = runTest {
        val weather = createWeatherEntity(
            city = "New York",
            country = "USA",
            temperatureCelsius = 25,
            sunrise = 1609459200L,
            sunset = 1609502400L,
            condition = "Clear",
            icon = "01d",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val retrievedWeather = weatherDao.getLatestWeather()
        assertNotNull(retrievedWeather)
        assertEquals("New York", retrievedWeather?.city)
        assertEquals("USA", retrievedWeather?.country)
        assertEquals(25, retrievedWeather?.temperatureCelsius)
        assertEquals(1609459200L, retrievedWeather?.sunrise)
        assertEquals(1609502400L, retrievedWeather?.sunset)
        assertEquals("Clear", retrievedWeather?.condition)
        assertEquals("01d", retrievedWeather?.icon)
        assertEquals("test@example.com", retrievedWeather?.userEmail)
    }

    @Test
    fun getLatestWeather_shouldReturnMostRecentWeatherEntry() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 5000
        )
        val weather3 = createWeatherEntity(
            city = "Tokyo",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val latestWeather = weatherDao.getLatestWeather()
        assertNotNull(latestWeather)
        assertEquals("Tokyo", latestWeather?.city)
    }

    @Test
    fun getLatestWeather_shouldReturnNullWhenNoWeatherExists() = runTest {
        val latestWeather = weatherDao.getLatestWeather()
        assertNull(latestWeather)
    }

    @Test
    fun getLatestWeather_shouldReturnLatestWeatherRegardlessOfUserEmail() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "user1@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "user2@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)

        val latestWeather = weatherDao.getLatestWeather()
        assertNotNull(latestWeather)
        assertEquals("London", latestWeather?.city)
        assertEquals("user2@example.com", latestWeather?.userEmail)
    }

    @Test
    fun insertWeather_shouldReplaceExistingWeatherWithSameId() = runTest {
        val weather1 = createWeatherEntity(
            id = 1,
            city = "New York",
            temperatureCelsius = 20,
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )
        val weather2 = createWeatherEntity(
            id = 1,
            city = "London",
            temperatureCelsius = 15,
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() + 1000
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)

        val latestWeather = weatherDao.getLatestWeather()
        assertNotNull(latestWeather)
        assertEquals("London", latestWeather?.city)
        assertEquals(15, latestWeather?.temperatureCelsius)
    }

    @Test
    fun getWeatherHistory_shouldReturnAllWeatherEntriesOrderedByCreatedAtDesc() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 20000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather3 = createWeatherEntity(
            city = "Tokyo",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val history = weatherDao.getWeatherHistory().first()

        assertEquals(3, history.size)
        assertEquals("Tokyo", history[0].city)
        assertEquals("London", history[1].city)
        assertEquals("New York", history[2].city)
    }

    @Test
    fun getWeatherHistory_shouldReturnEmptyListWhenNoWeatherExists() = runTest {
        val history = weatherDao.getWeatherHistory().first()
        assertTrue(history.isEmpty())
    }

    @Test
    fun getWeatherHistory_shouldReturnEntriesInDescendingOrderByCreatedAt() = runTest {
        val baseTime = System.currentTimeMillis()
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = baseTime - 20000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = baseTime - 10000
        )
        val weather3 = createWeatherEntity(
            city = "Tokyo",
            userEmail = "test@example.com",
            createdAt = baseTime
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val history = weatherDao.getWeatherHistory().first()

        assertEquals(3, history.size)
        assertTrue(history[0].createdAt > history[1].createdAt)
        assertTrue(history[1].createdAt > history[2].createdAt)
    }

    @Test
    fun getWeatherHistory_shouldIncludeWeatherFromAllUsers() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "user1@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "user2@example.com",
            createdAt = System.currentTimeMillis() - 5000
        )
        val weather3 = createWeatherEntity(
            city = "Tokyo",
            userEmail = "user3@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val history = weatherDao.getWeatherHistory().first()

        assertEquals(3, history.size)
        assertEquals("Tokyo", history[0].city)
        assertEquals("London", history[1].city)
        assertEquals("New York", history[2].city)
    }

    @Test
    fun getWeatherHistoryByEmail_shouldReturnWeatherForSpecificUserEmail() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "user1@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "user2@example.com",
            createdAt = System.currentTimeMillis() - 5000
        )
        val weather3 = createWeatherEntity(
            city = "Tokyo",
            userEmail = "user1@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val user1History = weatherDao.getWeatherHistoryByEmail("user1@example.com").first()
        val user2History = weatherDao.getWeatherHistoryByEmail("user2@example.com").first()

        assertEquals(2, user1History.size)
        assertEquals("Tokyo", user1History[0].city)
        assertEquals("New York", user1History[1].city)

        assertEquals(1, user2History.size)
        assertEquals("London", user2History[0].city)
    }

    @Test
    fun getWeatherHistoryByEmail_shouldReturnEmptyListWhenNoWeatherExistsForEmail() = runTest {
        val weather = createWeatherEntity(
            city = "New York",
            userEmail = "user1@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val user2History = weatherDao.getWeatherHistoryByEmail("user2@example.com").first()
        assertTrue(user2History.isEmpty())
    }

    @Test
    fun getWeatherHistoryByEmail_shouldReturnWeatherOrderedByCreatedAtDesc() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 20000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather3 = createWeatherEntity(
            city = "Tokyo",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val history = weatherDao.getWeatherHistoryByEmail("test@example.com").first()

        assertEquals(3, history.size)
        assertTrue(history[0].createdAt > history[1].createdAt)
        assertTrue(history[1].createdAt > history[2].createdAt)
    }

    @Test
    fun insertWeather_shouldHandleMultipleWeatherEntriesForSameUser() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 10000
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() - 5000
        )
        val weather3 = createWeatherEntity(
            city = "Paris",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)
        weatherDao.insertWeather(weather3)

        val history = weatherDao.getWeatherHistoryByEmail("test@example.com").first()
        assertEquals(3, history.size)
        assertEquals("Paris", history[0].city)
        assertEquals("London", history[1].city)
        assertEquals("New York", history[2].city)
    }

    @Test
    fun getWeatherHistoryByEmail_shouldBeCaseSensitiveForEmail() = runTest {
        val weather = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val history = weatherDao.getWeatherHistoryByEmail("TEST@EXAMPLE.COM").first()
        assertTrue(history.isEmpty())
    }

    @Test
    fun getWeatherHistoryByEmail_shouldReturnEmptyListForEmptyEmail() = runTest {
        val weather = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val history = weatherDao.getWeatherHistoryByEmail("").first()
        assertTrue(history.isEmpty())
    }

    @Test
    fun insertWeather_shouldGenerateAutoIncrementId() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() + 1000
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)

        val history = weatherDao.getWeatherHistory().first()
        assertEquals(2, history.size)
        assertTrue(history[0].id > 0)
        assertTrue(history[1].id > 0)
        assertNotEquals(history[0].id, history[1].id)
    }

    @Test
    fun getWeatherHistory_shouldUpdateWhenNewWeatherIsInserted() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        var history = weatherDao.getWeatherHistory().first()
        assertEquals(1, history.size)

        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() + 1000
        )

        weatherDao.insertWeather(weather2)
        history = weatherDao.getWeatherHistory().first()
        assertEquals(2, history.size)
        assertEquals("London", history[0].city)
        assertEquals("New York", history[1].city)
    }

    @Test
    fun getWeatherHistoryByEmail_shouldUpdateWhenNewWeatherIsInserted() = runTest {
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather1)
        var history = weatherDao.getWeatherHistoryByEmail("test@example.com").first()
        assertEquals(1, history.size)

        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis() + 1000
        )

        weatherDao.insertWeather(weather2)
        history = weatherDao.getWeatherHistoryByEmail("test@example.com").first()
        assertEquals(2, history.size)
        assertEquals("London", history[0].city)
        assertEquals("New York", history[1].city)
    }

    @Test
    fun insertWeather_shouldHandleWeatherWithSpecialCharactersInCity() = runTest {
        val weather = createWeatherEntity(
            city = "São Paulo",
            country = "Brazil",
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val retrievedWeather = weatherDao.getLatestWeather()
        assertNotNull(retrievedWeather)
        assertEquals("São Paulo", retrievedWeather?.city)
        assertEquals("Brazil", retrievedWeather?.country)
    }

    @Test
    fun insertWeather_shouldHandleWeatherWithNegativeTemperature() = runTest {
        val weather = createWeatherEntity(
            city = "Moscow",
            temperatureCelsius = -20,
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val retrievedWeather = weatherDao.getLatestWeather()
        assertNotNull(retrievedWeather)
        assertEquals(-20, retrievedWeather?.temperatureCelsius)
    }

    @Test
    fun insertWeather_shouldHandleWeatherWithZeroTemperature() = runTest {
        val weather = createWeatherEntity(
            city = "Freezing City",
            temperatureCelsius = 0,
            userEmail = "test@example.com",
            createdAt = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)

        val retrievedWeather = weatherDao.getLatestWeather()
        assertNotNull(retrievedWeather)
        assertEquals(0, retrievedWeather?.temperatureCelsius)
    }

    @Test
    fun getLatestWeather_shouldReturnLatestEvenWithSameTimestamp() = runTest {
        val timestamp = System.currentTimeMillis()
        val weather1 = createWeatherEntity(
            city = "New York",
            userEmail = "test@example.com",
            createdAt = timestamp
        )
        val weather2 = createWeatherEntity(
            city = "London",
            userEmail = "test@example.com",
            createdAt = timestamp
        )

        weatherDao.insertWeather(weather1)
        weatherDao.insertWeather(weather2)

        val latestWeather = weatherDao.getLatestWeather()
        assertNotNull(latestWeather)
        assertTrue(latestWeather?.city == "New York" || latestWeather?.city == "London")
    }

    private fun createWeatherEntity(
        id: Int = 0,
        city: String = "Test City",
        country: String = "US",
        temperatureCelsius: Int = 20,
        sunrise: Long = 1609459200L,
        sunset: Long = 1609502400L,
        condition: String = "Clear",
        icon: String = "01d",
        createdAt: Long = System.currentTimeMillis(),
        userEmail: String = "test@example.com"
    ): WeatherEntity {
        return WeatherEntity(
            id = id,
            city = city,
            country = country,
            temperatureCelsius = temperatureCelsius,
            sunrise = sunrise,
            sunset = sunset,
            condition = condition,
            icon = icon,
            createdAt = createdAt,
            userEmail = userEmail
        )
    }
}

