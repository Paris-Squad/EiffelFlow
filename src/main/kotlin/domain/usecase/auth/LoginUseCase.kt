package org.example.domain.usecase.auth

import org.example.data.repository.AuthRepositoryImpl

class LoginUseCase(private val authRepositoryImpl: AuthRepositoryImpl) {

    fun login(userName: String, password: String): Result<String> =
        authRepositoryImpl.loginUser(userName, password)
}
