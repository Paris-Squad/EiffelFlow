package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val hashPasswordUseCase: HashPasswordUseCase,
    private val auditRepository: AuditRepository
) {
    suspend fun updateUser(
        userName: String,
        currentPassword: String,
        newPassword: String
    ): User {

        verifySessionActive()

        validateCurrentPassword(currentPassword)

        val currentUser = SessionManger.getUser()
        val updatedUser = User(
            userId = currentUser.userId,
            username = userName,
            password = hashPasswordUseCase.hashPassword(newPassword),
            role = currentUser.role
        )
        val result = userRepository.updateUser(updatedUser)
        val auditLog = updatedUser.toAuditLog(
            editor = currentUser,
            actionType = AuditLogAction.UPDATE,
            changedField = "user",
            oldValue = currentUser.toString(),
            newValue = updatedUser.toString()
            )
            auditRepository.createAuditLog(auditLog)
        return result
    }

    private fun verifySessionActive() {
        require(SessionManger.isLoggedIn()) {
            throw EiffelFlowException.AuthorizationException("You must be logged in to update user")
        }
    }

    private fun validateCurrentPassword(currentPassword: String) {
        val currentUser = SessionManger.getUser()
        require(currentUser.password == currentPassword) {
            throw EiffelFlowException.AuthorizationException("Current password is not correct")
        }
    }
}