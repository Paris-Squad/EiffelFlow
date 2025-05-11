package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import java.util.UUID

class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val auditRepository: AuditRepository
) {
    suspend fun deleteUser(userId: UUID): User {
        validateAdminAccess()
        val deletedUser = userRepository.deleteUser(userId)

        val auditLog = deletedUser.toAuditLog(
               SessionManger.getUser(),
            AuditLogAction.DELETE,
            oldValue = deletedUser.toString()
           )
           auditRepository.createAuditLog(auditLog)
        return deletedUser
    }

    private fun validateAdminAccess() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Only admins can delete users")
        }
    }
}