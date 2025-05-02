package org.example.domain.usecase.auth

import org.example.data.storage.SessionManger
import org.example.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    fun logout(): Result<Unit> =
        authRepository.clearLogin().fold(onSuccess = { Result.success(Unit) }, onFailure = { Result.failure(it) })
}