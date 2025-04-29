package org.example.data.respoitory

import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.user.UserDataSource
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import org.example.domain.model.entities.User
import org.example.domain.repository.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.common.Constants.USER
import java.util.UUID

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val auditDataSource: AuditDataSource,
) : UserRepository {
    override fun createUser(user: User): Result<User> {
        return userDataSource.createUser(user).also { result ->
            result.onSuccess { createdUser ->
                val auditLog = AuditLog(
                    itemId = createdUser.userId,
                    itemName = USER,
                    userId = createdUser.userId,
                    userName = createdUser.username,
                    actionType = AuditAction.CREATE,
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