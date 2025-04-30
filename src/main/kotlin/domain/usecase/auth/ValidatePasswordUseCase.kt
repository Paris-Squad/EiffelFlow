package org.example.domain.usecase.auth

import org.example.domain.model.exception.EiffelFlowException.PasswordValidationException

class ValidatePasswordUseCase(
    private val validator: Validator
) {
    fun validatePassword(password: String): Result<Unit> {
        val validationResult = validator.validatePassword(password)

        return if (validationResult.isNotEmpty()) {
            Result.failure(PasswordValidationException(validationResult))
        } else Result.success(Unit)
    }
}