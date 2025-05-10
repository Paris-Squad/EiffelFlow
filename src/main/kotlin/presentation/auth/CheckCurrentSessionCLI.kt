package org.example.presentation.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.usecase.auth.CheckCurrentSessionUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.Printer

class CheckCurrentSessionCLI(
    private val checkCurrentSessionUseCase: CheckCurrentSessionUseCase,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            runBlocking {
               val currentSession = checkCurrentSessionUseCase.getCurrentSessionUser()
                if (currentSession == null) {
                    printer.displayLn("You are not logged in. Please login First.")
                }else{
                    printer.displayLn("Welcome back ${currentSession.username}")
                }
            }
        }
    }
}