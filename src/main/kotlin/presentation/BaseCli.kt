package org.example.presentation

import org.example.domain.exception.EiffelFlowException
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

abstract class BaseCli(private val printer: Printer) {
    protected fun tryStartCli(block: () -> Unit) {
        try {
            block()
        } catch (e: EiffelFlowException.AuthenticationException) {
            printer.displayLn("Password validation failed:${e.message}")
        }  catch (e: EiffelFlowException.IOException) {
            printer.displayLn("Something went wrong:${e.message}")
        } catch (e: EiffelFlowException.AuthorizationException) {
            printer.displayLn("Authorization failed:${e.message}")
        } catch (e: EiffelFlowException.NotFoundException) {
            printer.displayLn(e.message)
        } catch (e: Throwable) {
            printer.displayLn("An error occurred: ${e.message}")
        }
    }

    protected fun readCredentials(inputReader: InputReader): Pair<String, String>? {
        printer.displayLn("Enter user name:")
        val name = inputReader.readString()
        if (name.isNullOrBlank()) {
            printer.displayLn("user name cannot be empty.")
            return null
        }
        printer.displayLn("Enter password:")
        val password = inputReader.readString()
        if (password.isNullOrBlank()) {
            printer.displayLn("password cannot be empty.")
            return null
        }
        return Pair(name, password)
    }
}