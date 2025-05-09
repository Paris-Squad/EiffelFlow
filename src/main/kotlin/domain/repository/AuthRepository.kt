package org.example.domain.repository

import org.example.domain.model.User

interface AuthRepository {

    suspend fun saveUserLogin(user: User): User

    suspend fun isUserLoggedIn(): Boolean

    suspend fun clearLogin()

    suspend fun loginUser(username: String, password: String): User
}