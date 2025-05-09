package org.example.presentation.presenter

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LoginUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer

class LoginCLI(
    private val loginUseCase: LoginUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) {
    fun onLoginClicked(){
        try {
            printer.displayLn("Enter user name:")
            val name = inputReader.readString()
            if (name.isNullOrBlank()) {
                printer.displayLn("user name cannot be empty.")
                return
            }
            printer.displayLn("Enter password:")
            val password = inputReader.readString()
            if (password.isNullOrBlank()) {
                printer.displayLn("password cannot be empty.")
                return
            }

            login(userName = name, password = password)
            printer.displayLn("Login successful")

        } catch (e: EiffelFlowException.AuthorizationException) {
            printer.displayLn("Login Failed")
        }
    }


    fun login(userName: String, password: String) {
        return runBlocking {
                loginUseCase.login(userName = userName, password = password)
        }
    }

}

