package org.example.presentation.presenter.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LogoutUseCase

class LogoutPresenter(private  val logoutUseCase: LogoutUseCase) {

    fun logout(): String {
        return runBlocking {
            try {
                logoutUseCase.logout()
                "Logout successful"
            } catch (e: EiffelFlowException.AuthorizationException) {
                e.message ?: "Logout failed"
            }
        }
    }
}