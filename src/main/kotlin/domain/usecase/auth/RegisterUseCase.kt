package org.example.domain.usecase.auth

import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import kotlin.Result

class RegisterUseCase(
    private val userRepository: UserRepository,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateUsernameUseCase: ValidateUserNameUseCase,
    private val hashPasswordUseCase: HashPasswordUseCase
) {
    fun register(username: String, password: String, role: RoleType, creator: User): Result<User> {
        if (creator.role != RoleType.ADMIN) {
            return Result.failure(EiffelFlowException.AuthorizationException("Only admins can register users."))
        }

        val usernameValidation = validateUsername(username)
        if (usernameValidation.isFailure) return Result.failure(usernameValidation.exceptionOrNull()!!)

        val passwordValidation = validatePassword(password)
        if (passwordValidation.isFailure) return Result.failure(passwordValidation.exceptionOrNull()!!)

        val availabilityCheck = checkUsernameAvailability(username)
        if (availabilityCheck.isFailure) return Result.failure(availabilityCheck.exceptionOrNull()!!)

        return createUser(username, password, role, creator)
    }

    private fun validateUsername(username: String): Result<Unit> =
        validateUsernameUseCase.validateUserName(username)

    private fun validatePassword(password: String): Result<Unit> =
        validatePasswordUseCase.validatePassword(password)

    private fun checkUsernameAvailability(username: String): Result<Unit> {
        val usersResult = userRepository.getUsers()
        return usersResult.fold(
            onSuccess = {onCheckUsernameAvailability(username = username, existingUsers = it)},
            onFailure = { Result.failure(it) }
        )
    }

    private fun onCheckUsernameAvailability(username: String, existingUsers: List<User>): Result<Unit> {
        return if (existingUsers.any { it.username.equals(username, ignoreCase = true) }) {
            Result.failure(EiffelFlowException. AuthorizationException("Username '$username' is already taken. Please choose another username."))
        } else {
            Result.success(Unit)
        }
    }

    private fun createUser(username: String, password: String, role: RoleType, creator: User): Result<User> {
        val hashedPassword = hashPasswordUseCase.hashPassword(password)

        return userRepository.createUser(
            user = User(username = username, password = hashedPassword, role = role),
            createdBy = creator
        )
    }
}