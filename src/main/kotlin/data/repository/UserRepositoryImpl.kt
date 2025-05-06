package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import java.util.UUID

class UserRepositoryImpl(
    private val userCsvParser: UserCsvParser,
    private val fileDataSource: FileDataSource,
    private val auditRepository: AuditRepository,
) : UserRepository {
    override fun createUser(user: User): User {
        return try {
            val userAsCsv = userCsvParser.serialize(user)
            val users = getUsers()

            validateUsernameUniqueness(users, user.username)
            validateAdminPermission()

            fileDataSource.writeLinesToFile(userAsCsv)
            val auditLog = user.toAuditLog(SessionManger.getUser(), AuditLogAction.CREATE)
            auditRepository.createAuditLog(auditLog)
            user
        }catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't Create User because ${e.message}")
        }
    }

    private fun validateUsernameUniqueness(users: List<User>, username: String) {
        if (users.any { it.username.equals(username, ignoreCase = true) }) {
            throw EiffelFlowException.AuthorizationException("Username '$username' is already taken. Please choose another username.")
        }
    }

    private fun validateAdminPermission() {
        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Only admin can create user")
        }
    }

    override fun updateUser(user: User): User {
        return try {
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
        } catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't Update User because ${e.message}")
        }
    }

    override fun deleteUser(userId: UUID): User {
        return try {
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
        } catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't Delete User because ${e.message}")
        }
    }

    override fun getUserById(userId: UUID): User {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { line -> userCsvParser.parseCsvLine(line) }

            val user = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")
            user
        } catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't get User because ${e.message}")
        }
    }

    override fun getUsers(): List<User> {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { line -> userCsvParser.parseCsvLine(line) }
            users
        } catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't get Users because ${e.message}")
        }
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }
}
