package com.perennial.weather.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
object TestMainDispatcher {
    private val testDispatcher = StandardTestDispatcher()

    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    fun teardown() {
        Dispatchers.resetMain()
    }

    fun getDispatcher() = testDispatcher
}

