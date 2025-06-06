package org.example.data.local.csvrepository

import org.example.data.BaseRepository
import org.example.data.local.FileDataSource
import org.example.data.local.parser.UserCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class UserRepositoryImpl(
    private val userCsvParser: UserCsvParser,
    private val fileDataSource: FileDataSource,
) : BaseRepository(), UserRepository {
    override suspend fun createUser(user: User): User {
        return executeSafely {
            val userAsCsv = userCsvParser.serialize(user)
            val users = getUsers()

            validateUsernameUniqueness(users, user.username)
            fileDataSource.writeLinesToFile(userAsCsv)

            user
        }
    }

    private fun validateUsernameUniqueness(users: List<User>, username: String) {
        if (users.any { it.username.equals(username, ignoreCase = true) }) {
            throw EiffelFlowException.AuthorizationException("Username '$username' is already taken. Please choose another username.")
        }
    }

    override suspend fun updateUser(user: User): User {
        return executeSafely {
            val users = getUsers()
            val existingUser = users.find { it.userId == user.userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID ${user.userId} not found")

            val oldUserCsv = userCsvParser.serialize(existingUser)
            val newUserCsv = userCsvParser.serialize(user)

            fileDataSource.updateLinesToFile(newUserCsv, oldUserCsv)
            user
        }
    }

    override suspend fun deleteUser(userId: UUID): User {
        return executeSafely {
            val users = getUsers()
            val userToDelete = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")

            val userCsv = userCsvParser.serialize(userToDelete)
            fileDataSource.deleteLineFromFile(userCsv)

            userToDelete
        }
    }

    override suspend fun getUserById(userId: UUID): User {
        return executeSafely {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { line -> userCsvParser.parseCsvLine(line) }

            val user = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")
            user
        }
    }

    override suspend fun getUsers(): List<User> {
        return executeSafely {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { line -> userCsvParser.parseCsvLine(line) }
            users
        }
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }
}
