package org.example.data.repository

import org.example.data.storage.auth.AuthDataSource
import org.example.domain.repository.AuthRepository
import java.util.UUID

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override fun saveUserLogin(userID: UUID): Result<Boolean> =
        authDataSource.saveUserLogin(userID)

    override fun getIsUserLoggedIn(): Result<Boolean> =
        authDataSource.getIsUserLoggedIn()

    override fun clearLogin(): Result<Boolean> =
        authDataSource.clearLogin()
}