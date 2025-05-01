package org.example.domain.repository

import java.util.UUID

interface AuthRepository {
    fun saveUserLogin(userID: UUID): Result<Boolean>
    fun getIsUserLoggedIn(): Result<Boolean>
    fun clearLogin(): Result<Boolean>
}