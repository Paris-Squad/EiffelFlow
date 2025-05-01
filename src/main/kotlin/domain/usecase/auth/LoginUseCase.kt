package org.example.domain.usecase.auth


import org.example.domain.utils.ValidationErrorMessage
import org.example.data.repository.AuthRepositoryImpl
import org.example.domain.model.User
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.UserRepository


class LoginUseCase(
    private val userRepository: UserRepository,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateUsernameUseCase: ValidateUserNameUseCase,
    private val authRepositoryImpl: AuthRepositoryImpl,
    ){
    fun login(userName: String,password: String): Result<String>{
        val usernameValidation = validateUsername(userName)
        if (usernameValidation.isFailure) return Result.failure(usernameValidation.exceptionOrNull()!!)

        val passwordValidation = validatePassword(password)
        if (passwordValidation.isFailure) return Result.failure(passwordValidation.exceptionOrNull()!!)

        return userRepository.getUsers().mapCatching{users-> validateUser(users,userName,password)}
            .onSuccess { user-> authRepositoryImpl.saveUserLogin(userID = user.userId) }
            .map { "Login successfully" }
    }

    private fun validateUsername(username: String): Result<Unit> =
        validateUsernameUseCase.validateUserName(username)

    private fun validatePassword(password: String): Result<Unit> =
        validatePasswordUseCase.validatePassword(password)

    private fun validateUser(users: List<User>, username: String, password: String): User{
        val user=users.find { it.username==username }
            ?: throw EiffelFlowException.AuthenticationException(setOf(ValidationErrorMessage.INVALID_USERNAME))
        if(user.password!=password){
            throw EiffelFlowException.AuthenticationException(setOf(ValidationErrorMessage.INVALID_PASSWORD))
        }
        return user
    }
}

