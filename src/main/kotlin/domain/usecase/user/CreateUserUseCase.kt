package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val hashPasswordUseCase: HashPasswordUseCase,
    private val auditRepository: AuditRepository
) {
    suspend fun register(
        username: String,
        password: String,
        userRole: RoleType
    ): User {
        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Only admin can create or update user")
        }

        val hashedPassword = hashPasswordUseCase.hashPassword(password)
        val createdUser = userRepository.createUser(
            user = User(username = username, password = hashedPassword, role = userRole)
        )
         val auditLog = createdUser.toAuditLog(SessionManger.getUser(), AuditLogAction.CREATE)
         auditRepository.createAuditLog(auditLog)
        return createdUser
    }
}