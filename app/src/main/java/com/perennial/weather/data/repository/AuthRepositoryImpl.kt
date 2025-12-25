package com.perennial.weather.data.repository

import com.perennial.weather.data.local.dao.UserDao
import com.perennial.weather.data.local.entity.UserEntity
import com.perennial.weather.domain.repository.AuthRepository
import com.perennial.weather.utils.PasswordUtils
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository{
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Boolean {
        val userExist = userDao.getUserByEmail(email = email)

        val hashedPassword = PasswordUtils.hashPassword(password = password)
        if (userExist != null){
            return false
        }else{
            val newUser = UserEntity(
                name = name,
                email = email,
                password = hashedPassword
            )
            userDao.registerUser(newUser)
            return true
        }
    }

    override suspend fun login(email: String, password: String): Boolean {
        val hashedPassword = PasswordUtils.hashPassword(password = password)
        val user = userDao.login(email = email, password = hashedPassword)
        return user != null
    }
}