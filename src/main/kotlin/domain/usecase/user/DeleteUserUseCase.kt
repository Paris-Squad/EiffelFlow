package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class DeleteUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend fun deleteUser(userId: UUID): User {
        validateAdminAccess()
        return userRepository.deleteUser(userId)
    }

    private fun validateAdminAccess() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Only admins can delete users")
        }
    }
}