package org.example.domain.usecase.auth

import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.UserRepository

class RegisterUseCase(
    private val userRepository: UserRepository, private val hashPasswordUseCase: HashPasswordUseCase
) {
    fun register(username: String, password: String, userRole: RoleType): User {
        val hashedPassword = hashPasswordUseCase.hashPassword(password)
        return userRepository.createUser(user = User(username = username, password = hashedPassword, role = userRole))
    }
}