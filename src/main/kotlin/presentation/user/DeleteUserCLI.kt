package org.example.presentation.user

import kotlinx.coroutines.runBlocking
import org.example.domain.model.User
import org.example.domain.usecase.user.DeleteUserUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class DeleteUserCLI(
    private val deleteUserUseCase: DeleteUserUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            printer.displayLn("Enter User ID to delete: ")
            val input = inputReader.readString()
            if (input.isNullOrBlank()) {
                printer.displayLn("User ID cannot be empty.")
                return@tryStartCli
            }

            val userId = UUID.fromString(input.trim())
            val deletedUser = deleteUser(userId)
            printer.displayLn("User deleted successfully $deletedUser")
        }
    }

    private fun deleteUser(userId: UUID): User = runBlocking {
        deleteUserUseCase.deleteUser(userId)
    }

}