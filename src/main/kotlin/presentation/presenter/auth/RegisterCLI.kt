package org.example.presentation.presenter.auth

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer

class RegisterCLI(
    private val registerUseCase: RegisterUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) {
    fun onRegisterClick(){
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

            printer.displayLn("Select a role:")
            RoleType.entries.forEachIndexed { index, role ->
                printer.displayLn("${index + 1}. ${role.name}")
            }

            val roleInput = inputReader.readString()?.toIntOrNull()
            val role = if (roleInput != null && roleInput in 1..RoleType.entries.size) {
                RoleType.entries[roleInput - 1]
            } else {
                printer.displayLn("Invalid role selection.")
                return
            }

            register(username = name, password = password,role=role)
            printer.displayLn("Registration successful")

        } catch (e: EiffelFlowException.AuthorizationException) {
            printer.displayLn("Register Failed")
        }  catch (e: Exception) {
            printer.displayLn("An error occurred during registration: ${e.message}")
        }
    }


    fun register(username: String, password: String, role: RoleType): User {
        return runBlocking {
                registerUseCase.register(
                    username = username,
                    password = password,
                    userRole = role
                )
            }
    }

}