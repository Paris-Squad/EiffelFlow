package org.example.domain.usecase.auth

import org.example.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {

    fun login(userName: String, password: String): Result<String> =
        authRepository.loginUser(userName, password)
}
