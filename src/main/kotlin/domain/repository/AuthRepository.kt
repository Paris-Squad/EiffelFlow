package org.example.domain.repository

import org.example.domain.model.User

interface AuthRepository {
    fun saveUserLogin(user: User): Result<Boolean>
    fun getIsUserLoggedIn(): Result<Boolean>
    fun clearLogin(): Result<Boolean>
}