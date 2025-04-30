package org.example.domain.usecase.auth


import org.example.common.ValidationMessages
import org.example.domain.model.entities.User
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.UserRepository


class LoginUseCase(private val userRepository: UserRepository){
    fun login(userName: String,password: String): Result<String>{
        return userRepository.getUsers().mapCatching{users-> validateUser(users,userName,password)}
       .map { "Login successfully" }
    }
}

private fun validateUser(users: List<User>, username: String, password: String): User{
    val user=users.find { it.username==username }
        ?: throw EiffelFlowException.UserNameValidationException(setOf(ValidationMessages.ValidationRule.INVALID_USERNAME))
    if(user.password!=password){
        throw EiffelFlowException.PasswordValidationException(setOf(ValidationMessages.ValidationRule.INVALID_PASSWORD))
    }
     return user
}