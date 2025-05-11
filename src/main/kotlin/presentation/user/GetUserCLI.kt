package org.example.presentation.user

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
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

    fun start() {
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
        try {
            printer.displayLn("Enter User ID : ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("user ID cannot be empty.")
                return
            }

            val userId = UUID.fromString(input.trim())
            val user = getUserById(userId)
            printer.displayLn("user details : ${user.userId} - ${user.username} - ${user.role} ")

        } catch (_: IllegalArgumentException) {
            printer.displayLn("Invalid UUID format.")
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while retrieving user: ${e.message}", e)
        }
    }

    private fun getUserById(userId: UUID): User {
        return runBlocking {
            getUserUseCase.getUserById(userId)
        }
    }


}