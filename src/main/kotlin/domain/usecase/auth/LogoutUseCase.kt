package org.example.domain.usecase.auth

import org.example.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend fun logout() = authRepository.clearLogin()
}