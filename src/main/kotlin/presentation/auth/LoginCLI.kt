package org.example.presentation.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.usecase.auth.LoginUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class LoginCLI(
    private val loginUseCase: LoginUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        tryStartCli {
            val credentials = readCredentials(inputReader)
            if (credentials == null) {
                return@tryStartCli
            }

            val (name, password) = credentials
            login(userName = name, password = password)
            printer.displayLn("Login successful")
        }
    }


    fun login(userName: String, password: String) {
        return runBlocking {
            loginUseCase.login(userName = userName, password = password)
        }
    }

}