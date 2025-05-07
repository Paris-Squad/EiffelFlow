package org.example.domain.usecase.auth

import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend fun login(userName: String, password: String): User =
        authRepository.loginUser(userName, password)
}