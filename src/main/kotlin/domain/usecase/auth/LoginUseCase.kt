package org.example.domain.usecase.auth


import org.example.common.Constants
import org.example.domain.model.entities.User
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.UserRepository


class LoginUseCase(
    private val userRepository: UserRepository,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateUsernameUseCase: ValidateUserNameUseCase,
    ){
    fun login(userName: String,password: String): Result<String>{
        val usernameValidation = validateUsername(userName)
        if (usernameValidation.isFailure) return Result.failure(usernameValidation.exceptionOrNull()!!)

        val passwordValidation = validatePassword(password)
        if (passwordValidation.isFailure) return Result.failure(passwordValidation.exceptionOrNull()!!)
        return userRepository.getUsers().mapCatching{users-> validateUser(users,userName,password)}
       .map { "Login successfully" }
    }

    private fun validateUsername(username: String): Result<Unit> =
        validateUsernameUseCase.validateUserName(username)

    private fun validatePassword(password: String): Result<Unit> =
        validatePasswordUseCase.validatePassword(password)

    private fun validateUser(users: List<User>, username: String, password: String): User{
        val user=users.find { it.username==username }
            ?: throw EiffelFlowException.UserNameValidationException(setOf(Constants.ValidationRule.INVALID_USERNAME))
        if(user.password!=password){
            throw EiffelFlowException.PasswordValidationException(setOf(Constants.ValidationRule.INVALID_PASSWORD))
        }
        return user
    }
}

