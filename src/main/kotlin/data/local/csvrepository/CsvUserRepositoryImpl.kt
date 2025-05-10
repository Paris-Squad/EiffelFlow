package org.example.data.local.csvrepository

import org.example.data.BaseRepository
import org.example.data.local.FileDataSource
import org.example.data.local.parser.UserCsvParser
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import java.util.UUID

class CsvUserRepositoryImpl(
    private val userCsvParser: UserCsvParser,
    private val fileDataSource: FileDataSource,
    private val auditRepository: AuditRepository,
) : BaseRepository(), UserRepository {
    override suspend fun createUser(user: User): User {
        return wrapInTryCatch {
            validateAdminPermission()
            val userAsCsv = userCsvParser.serialize(user)
            val users = getUsers()

            validateUsernameUniqueness(users, user.username)

            fileDataSource.writeLinesToFile(userAsCsv)
            val auditLog = user.toAuditLog(SessionManger.getUser(), AuditLogAction.CREATE)
            auditRepository.createAuditLog(auditLog)
            user
        }
    }

    private fun validateUsernameUniqueness(users: List<User>, username: String) {
        if (users.any { it.username.equals(username, ignoreCase = true) }) {
            throw EiffelFlowException.AuthorizationException("Username '$username' is already taken. Please choose another username.")
        }
    }

    private fun validateAdminPermission() {
        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Only admin can create or update user")
        }
    }

    override suspend fun updateUser(user: User): User {
        return wrapInTryCatch {
            val users = getUsers()
            val existingUser = users.find { it.userId == user.userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID ${user.userId} not found")

            val oldUserCsv = userCsvParser.serialize(existingUser)
            val newUserCsv = userCsvParser.serialize(user)

            fileDataSource.updateLinesToFile(newUserCsv, oldUserCsv)

            val auditLog = user.toAuditLog(
                SessionManger.getUser(),
                AuditLogAction.UPDATE,
                changedField = "user",
                oldValue = existingUser.toString()
            )
            auditRepository.createAuditLog(auditLog)
            user
        }
    }

    override suspend fun deleteUser(userId: UUID): User {
        return wrapInTryCatch {
            validateAdminPermission()
            val users = getUsers()
            val userToDelete = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")

            val userCsv = userCsvParser.serialize(userToDelete)
            fileDataSource.deleteLineFromFile(userCsv)

            val auditLog = userToDelete.toAuditLog(
                SessionManger.getUser(), AuditLogAction.DELETE, oldValue = userToDelete.toString()
            )
            auditRepository.createAuditLog(auditLog)

            userToDelete
        }
    }

    override suspend fun getUserById(userId: UUID): User {
        return wrapInTryCatch {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { line -> userCsvParser.parseCsvLine(line) }

            val user = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")
            user
        }
    }

    override suspend fun getUsers(): List<User> {
        return wrapInTryCatch {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { line -> userCsvParser.parseCsvLine(line) }
            users
        }
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }
}
