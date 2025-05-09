package org.example.presentation.presenter.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.usecase.auth.RegisterUseCase

class RegisterPresenter(
    private val registerUseCase: RegisterUseCase
) {
    fun register(username: String, password: String, role: RoleType): User {
        return try {
            runBlocking {
                registerUseCase.register(
                    username = username,
                    password = password,
                    userRole = role
                )
            }
        } catch (e: EiffelFlowException.AuthorizationException) {
            throw e
        }  catch (e: Exception) {
            throw RuntimeException("An error occurred during registration: ${e.message}", e)
        }
    }
}