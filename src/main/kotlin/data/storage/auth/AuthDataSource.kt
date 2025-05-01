package org.example.data.storage.auth

import java.util.UUID

interface AuthDataSource {
    fun saveUserLogin(userID: UUID): Result<Boolean>
    fun getIsUserLoggedIn(): Result<Boolean>
    fun clearLogin() : Result<Boolean>
}