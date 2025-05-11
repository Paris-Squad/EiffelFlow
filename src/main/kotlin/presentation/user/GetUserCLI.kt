package org.example.presentation.user

import kotlinx.coroutines.runBlocking
import org.example.domain.model.User
import org.example.domain.usecase.user.GetUserUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class GetUserCLI(
    private val getUserUseCase: GetUserUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun viewAllUsers() {
        tryStartCli {
            val users = getUsers()
            if (users.isEmpty()) {
                printer.displayLn("No users found.")
            } else {
                users.forEachIndexed { index, user ->
                    printer.displayLn("${index + 1}. ${user.userId} - ${user.username}")
                }
            }
        }

    }

    private fun getUsers(): List<User> {
        return runBlocking {
            getUserUseCase.getUsers()
        }
    }

    fun displayUserById() {
        tryStartCli {
            printer.displayLn("Enter User ID : ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("user ID cannot be empty.")
                return@tryStartCli
            }

            val userId = UUID.fromString(input.trim())
            val user = getUserById(userId)
            printer.displayLn("user details : ${user.userId} - ${user.username} - ${user.role} ")

        }
    }

    private fun getUserById(userId: UUID): User {
        return runBlocking {
            getUserUseCase.getUserById(userId)
        }
    }


}