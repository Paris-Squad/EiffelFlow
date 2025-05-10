package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun updateUser(user: User): User {
        validateAdminAccess()
        return userRepository.updateUser(user)
    }

    private fun validateAdminAccess() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Only admins can update users")
        }
    }
}