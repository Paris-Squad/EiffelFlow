package org.example.presentation.user

import kotlinx.coroutines.runBlocking
import org.example.domain.model.User
import org.example.domain.usecase.user.UpdateUserUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class UpdateUserCLI(
    private val updateUserUseCase: UpdateUserUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {

            val userName = readNonBlankInput(
                inputReader = inputReader,
                prompt = "Enter new user name: ",
                errorMessage = "User name cannot be empty or null."
            ) ?: return@tryStartCli

            val currentPassword = readNonBlankInput(
                inputReader = inputReader,
                prompt = "Enter the current password: ",
                errorMessage = "Password cannot be empty or null."
            ) ?: return@tryStartCli

            val newPassword = readNonBlankInput(
                inputReader = inputReader,
                prompt = "Enter the new password: ",
                errorMessage = "Password cannot be empty or null."
            ) ?: return@tryStartCli

            val updatedUser = updateUser(userName, currentPassword, newPassword)
            printer.displayLn("User updated successfully $updatedUser")
        }
    }

    private fun updateUser(
        userName: String,
        currentPassword: String,
        newPassword: String
    ): User = runBlocking {
        updateUserUseCase.updateUser(userName, currentPassword, newPassword)
    }

}