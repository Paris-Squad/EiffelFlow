package org.example.domain.usecase.user

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class GetUserUseCase(
    private val repository: UserRepository
) {

    suspend fun getUsers(): List<User> {
        validateAdminPermission()
        return repository.getUsers()
    }

    suspend fun getUserById(userId: UUID): User{
        validateAdminPermission()
        return repository.getUserById(userId)
    }

    private fun validateAdminPermission() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to view users")
        }
    }
}