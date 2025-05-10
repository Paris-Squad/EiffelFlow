package org.example.presentation.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LogoutUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.Printer

class LogoutCLI(
    private val logoutUseCase: LogoutUseCase,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        try {
            logout()
            printer.displayLn("Logout successful")
        } catch (_: EiffelFlowException.AuthorizationException) {
            printer.displayLn("Logout failed")
        }
    }

    private fun logout() = runBlocking {
        logoutUseCase.logout()
    }
}