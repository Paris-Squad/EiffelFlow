package org.example.data.repository

import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.user.UserDataSource
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import java.util.UUID

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val auditDataSource: AuditDataSource,
) : UserRepository {
    override fun createUser(user: User, createdBy: User): Result<User> {
        return userDataSource.createUser(user).also { result ->
            result.onSuccess { createdUser ->
                val auditLog = AuditLog(
                    itemId = createdUser.userId,
                    itemName = createdUser.username,
                    userId = createdUser.userId,
                    editorName = createdBy.username,
                    actionType = AuditLogAction.CREATE,
                    auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    changedField = null,
                    oldValue = null,
                    newValue = createdUser.toString()
                )
                auditDataSource.createAuditLog(auditLog)
            }
        }
    }

    override fun updateUser(user: User): Result<User> {
        TODO("Not yet implemented")
    }

    override fun deleteUser(userId: UUID): Result<User> {
        TODO("Not yet implemented")
    }

    override fun getUserById(userId: UUID): Result<User> {
        TODO("Not yet implemented")
    }

    override fun getUsers(): Result<List<User>> {
        return userDataSource.getUsers()
    }

}