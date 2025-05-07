package org.example.presentation.presenter

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LoginUseCase

class LoginPresenter(private val loginUseCase: LoginUseCase) {
    fun onLoginClicked(userName: String, password: String): String {
        return runBlocking {
            try {
                loginUseCase.login(userName = userName, password = password)
                "Login successful"
            } catch (e: EiffelFlowException.AuthorizationException) {
                e.message ?: "Login failed"
            }
        }
    }
}

