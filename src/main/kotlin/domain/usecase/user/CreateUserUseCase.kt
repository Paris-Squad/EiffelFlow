package org.example.domain.usecase.user

import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase

class CreateUserUseCase(
    private val userRepository: UserRepository, private val hashPasswordUseCase: HashPasswordUseCase
) {
    suspend fun register(username: String, password: String, userRole: RoleType): User {
        val hashedPassword = hashPasswordUseCase.hashPassword(password)
        return userRepository.createUser(user = User(username = username, password = hashedPassword, role = userRole))
    }
}