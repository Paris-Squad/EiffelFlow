package org.example.presentation.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LogoutUseCase
import org.example.presentation.io.Printer

class LogoutCLI(
    private  val logoutUseCase: LogoutUseCase ,
    private val printer: Printer
) {

    fun onLogoutClick(){
        try {
            logout()
            printer.displayLn("Logout successful")
        } catch (e: EiffelFlowException.AuthorizationException) {
            printer.displayLn("Logout failed")
        }
    }

    fun logout(){
        return runBlocking {
                logoutUseCase.logout()
        }
    }
}