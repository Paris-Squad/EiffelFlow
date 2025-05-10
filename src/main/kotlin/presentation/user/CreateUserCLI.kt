package org.example.presentation.user

import kotlinx.coroutines.runBlocking
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.usecase.user.CreateUserUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class CreateUserCLI(
    private val createUserUseCase: CreateUserUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        onRegisterClick()
    }

    fun onRegisterClick() {
        tryStartCli {
            val credentials = readCredentials(inputReader)
            if (credentials == null) {
                return@tryStartCli
            }

            val (name, password) = credentials

            printer.displayLn("Select a role:")
            RoleType.entries.forEachIndexed { index, role ->
                printer.displayLn("${index + 1}. ${role.name}")
            }

            val roleInput = inputReader.readString()?.toIntOrNull()
            val role = if (roleInput != null && roleInput in 1..RoleType.entries.size) {
                RoleType.entries[roleInput - 1]
            } else {
                printer.displayLn("Invalid role selection.")
                return@tryStartCli
            }

            register(username = name, password = password, role = role)
            printer.displayLn("Registration successful")
        }
    }


    fun register(username: String, password: String, role: RoleType): User {
        return runBlocking {
            createUserUseCase.register(
                username = username,
                password = password,
                userRole = role
            )
        }
    }

}