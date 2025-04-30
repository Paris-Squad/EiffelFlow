package org.example.domain.usecase.auth

import org.example.domain.model.exception.EiffelFlowException.PasswordValidationException
import org.example.common.ValidationMessages

class ValidatePasswordUseCase {
    fun validatePassword(password: String): Result<Unit> {
        val validationResult = getPasswordValidationErrors(password)

        return if (validationResult.isNotEmpty()) {
            Result.failure(PasswordValidationException(validationResult))
        } else Result.success(Unit)
    }

    fun getPasswordValidationErrors(password: String): Set<ValidationMessages.ValidationRule> {
        val errors = mutableSetOf<ValidationMessages.ValidationRule>()

        if (password.length < MIN_PASSWORD_LENGTH)
            errors.add(ValidationMessages.ValidationRule.PASSWORD_TOO_SHORT)

        if (!password.any { it.isUpperCase() })
            errors.add(ValidationMessages.ValidationRule.PASSWORD_NO_UPPERCASE)

        if (!password.any { it.isLowerCase() })
            errors.add(ValidationMessages.ValidationRule.PASSWORD_NO_LOWERCASE)

        if (!password.any { it.isDigit() })
            errors.add(ValidationMessages.ValidationRule.PASSWORD_NO_DIGIT)

        if (!SPECIAL_CHAR_REGEX.matches(password))
            errors.add(ValidationMessages.ValidationRule.PASSWORD_NO_SPECIAL_CHAR)

        return errors
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        private val SPECIAL_CHAR_REGEX = Regex(".*[!@#$%^&*()\\-_+={}\\[\\]|\\\\:;\"'<>,.?/].*")
    }
}