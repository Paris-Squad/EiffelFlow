package org.example.domain.usecase.auth

import org.example.domain.utils.ValidationErrorMessage
import org.example.domain.exception.EiffelFlowException.AuthenticationException

class ValidateUserNameUseCase {
    fun validateUserName(userName: String) {
        val validationResult = getUserNameValidationErrors(userName)

         if (validationResult.isNotEmpty()) {
           throw AuthenticationException(validationResult)
        }
    }

    fun getUserNameValidationErrors(userName: String): Set<ValidationErrorMessage> {
        val errors = mutableSetOf<ValidationErrorMessage>()

        if (userName.length < MIN_USERNAME_LENGTH)
            errors.add(ValidationErrorMessage.USERNAME_TOO_SHORT)

        if (userName.length > MAX_USERNAME_LENGTH)
            errors.add(ValidationErrorMessage.USERNAME_TOO_LONG)

        if (!USERNAME_REGEX.matches(userName))
            errors.add(ValidationErrorMessage.USERNAME_INVALID_CHARACTERS)

        return errors
    }

    companion object {
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 30
        private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]+$")
    }


}