package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val hashPasswordUseCase: HashPasswordUseCase
) {
    suspend fun updateUser(
        userName: String,
        currentPassword: String,
        newPassword: String
    ): User {

        validateAdminAccess()
        validateCurrentPassword(currentPassword)

        val currentUser = SessionManger.getUser()
        val updatedUser = User(
            userId = currentUser.userId,
            username = userName,
            password = hashPasswordUseCase.hashPassword(newPassword),
            role = currentUser.role
        )
        return userRepository.updateUser(updatedUser)
    }

    private fun validateAdminAccess() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Only admins can update users")
        }
    }

    private fun validateCurrentPassword(currentPassword: String) {
        val currentUser = SessionManger.getUser()
        require(currentUser.password == currentPassword) {
            throw EiffelFlowException.AuthorizationException("Current password is not correct")
        }
    }
}