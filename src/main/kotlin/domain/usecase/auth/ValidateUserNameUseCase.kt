package org.example.domain.usecase.auth

import org.example.common.Constants
import org.example.domain.model.exception.EiffelFlowException.UserNameValidationException

class ValidateUserNameUseCase {
    fun validateUserName(userName: String): Result<Unit> {
        val validationResult = getUserNameValidationErrors(userName)

        return if (validationResult.isNotEmpty()) {
            Result.failure(UserNameValidationException(validationResult))
        } else Result.success(Unit)
    }

    fun getUserNameValidationErrors(userName: String): Set<Constants.ValidationRule> {
        val errors = mutableSetOf<Constants.ValidationRule>()

        if (userName.length < MIN_USERNAME_LENGTH)
            errors.add(Constants.ValidationRule.USERNAME_TOO_SHORT)

        if (userName.length > MAX_USERNAME_LENGTH)
            errors.add(Constants.ValidationRule.USERNAME_TOO_LONG)

        if (!USERNAME_REGEX.matches(userName))
            errors.add(Constants.ValidationRule.USERNAME_INVALID_CHARACTERS)

        return errors
    }

    companion object {
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 30
        private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]+$")
    }


}