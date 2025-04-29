package org.example.domain.usecase.auth

import org.example.domain.model.exception.EiffelFlowException.UserNameValidationException

class ValidateUserNameUseCase(private val validator: Validator) {
    fun validateUserName(userName: String): Result<Unit> {
        val validationResult = validator.validateUserName(userName)

        return if (validationResult.isNotEmpty()) {
            Result.failure(UserNameValidationException(validationResult))
        } else Result.success(Unit)
    }
}