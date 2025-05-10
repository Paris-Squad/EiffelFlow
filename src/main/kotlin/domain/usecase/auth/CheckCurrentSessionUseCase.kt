package org.example.domain.usecase.auth

import org.example.data.utils.SessionManger
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class CheckCurrentSessionUseCase(private val authRepository: AuthRepository) {

    suspend fun getCurrentSessionUser(): User? {
        val isUserLoggedIn = authRepository.isUserLoggedIn()
        return if (isUserLoggedIn) {
            SessionManger.getUser()
        } else {
            null
        }
    }

}