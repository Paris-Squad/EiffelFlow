package org.example.presentation

import org.example.domain.exception.EiffelFlowException
import org.example.presentation.io.Printer

abstract class BaseCli(private val printer: Printer) {
    protected fun tryStartCli(block: () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            printer.displayLn("An error occurred: ${e.message}")
        } catch (e: EiffelFlowException) {
            printer.displayLn("Failed to create the task: ${e.message}")
        } catch (e: Throwable) {
            printer.displayLn("An error occurred: ${e.message}")
        }
    }
}