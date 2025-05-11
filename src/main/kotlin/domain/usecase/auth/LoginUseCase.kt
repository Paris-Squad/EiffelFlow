package org.example.domain.usecase.auth

import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository, private val hashPasswordUseCase: HashPasswordUseCase) {

    suspend fun login(userName: String, password: String): User {
        val hashedPassword = hashPasswordUseCase.hashPassword(password)
        return authRepository.loginUser(userName, hashedPassword)
    }
}