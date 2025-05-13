package org.example.domain.usecase.user

import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class GetUserUseCase(
    private val repository: UserRepository
) {

    suspend fun getUsers(): List<User> {
        return repository.getUsers()
    }

    suspend fun getUserById(userId: UUID): User{
        return repository.getUserById(userId)
    }

}