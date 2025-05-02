package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.mapper.UserCsvMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import java.io.FileNotFoundException
import java.util.UUID
import kotlin.Result

class UserRepositoryImpl(
    private val userMapper: UserCsvMapper,
    private val csvManager: FileDataSource,
    private val auditRepository: AuditRepository,
) : UserRepository {
    override fun createUser(user: User, createdBy: User): Result<User> {
        return runCatching {
            val userAsCsv = userMapper.mapTo(user)
            csvManager.writeLinesToFile(userAsCsv)
            val auditLog = user.toAuditLog(createdBy, AuditLogAction.CREATE)
            auditRepository.createAuditLog(auditLog)
            user
        }.recoverCatching {
            when (it) {
                is EiffelFlowException -> throw it
                else -> throw EiffelFlowException.IOException(it.message)
            }
        }
    }

    override fun updateUser(user: User): Result<User> {
        return runCatching {
            val users = getUsers().getOrThrow()
            val existingUser = users.find { it.userId == user.userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID ${user.userId} not found")

            val oldUserCsv = userMapper.mapTo(existingUser)
            val newUserCsv = userMapper.mapTo(user)

            csvManager.updateLinesToFile(newUserCsv, oldUserCsv)

            val auditLog = user.toAuditLog(
                SessionManger.getUser(), 
                AuditLogAction.UPDATE, 
                changedField = "user", 
                oldValue = existingUser.toString()
            )
            auditRepository.createAuditLog(auditLog)

            user
        }.recoverCatching {
            when (it) {
                is EiffelFlowException -> throw it
                else -> throw EiffelFlowException.IOException(it.message)
            }
        }
    }

    override fun deleteUser(userId: UUID): Result<User> {
        return runCatching {
            val users = getUsers().getOrThrow()
            val userToDelete = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")

            val userCsv = userMapper.mapTo(userToDelete)
            csvManager.deleteLineFromFile(userCsv)

            val auditLog = userToDelete.toAuditLog(
                SessionManger.getUser(), 
                AuditLogAction.DELETE, 
                oldValue = userToDelete.toString()
            )
            auditRepository.createAuditLog(auditLog)

            userToDelete
        }.recoverCatching {
            when (it) {
                is EiffelFlowException -> throw it
                else -> throw EiffelFlowException.IOException(it.message)
            }
        }
    }

    override fun getUserById(userId: UUID): Result<User> {
        return runCatching {
            val lines = csvManager.readLinesFromFile()
            val users = lines
                .filter { it.isNotBlank() }
                .map { line -> userMapper.mapFrom(line) }

            val user = users.find { it.userId == userId }
                ?: throw EiffelFlowException.NotFoundException("User with ID $userId not found")
            user
        }.recoverCatching { it ->
            when (it) {
                is EiffelFlowException -> throw it
                else -> throw EiffelFlowException.IOException(it.message)
            }
        }
    }

    override fun getUsers(): Result<List<User>> {
        return runCatching {
            val lines = csvManager.readLinesFromFile()
            val users = lines
                .filter { it.isNotBlank() }
                .map { line -> userMapper.mapFrom(line) }

            users
        }.recoverCatching { e ->
            when (e) {
                is FileNotFoundException -> emptyList()
                else -> throw EiffelFlowException.IOException(e.message)
            }
        }
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }
}
