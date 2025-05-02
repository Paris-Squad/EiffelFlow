package org.example.domain.repository

import org.example.domain.model.User

interface AuthRepository {
    fun saveUserLogin(user: User): Result<User>
    fun isUserLoggedIn(): Result<Boolean>
    fun clearLogin(): Result<Boolean>
    fun loginUser(username: String, password: String): Result<String>
}