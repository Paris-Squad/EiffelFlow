package org.example.domain.usecase.auth

import org.example.domain.model.exception.EiffelFlowException.PasswordValidationException
import org.example.common.Constants

class ValidatePasswordUseCase {
    fun validatePassword(password: String): Result<Unit> {
        val validationResult = getPasswordValidationErrors(password)

        return if (validationResult.isNotEmpty()) {
            Result.failure(PasswordValidationException(validationResult))
        } else Result.success(Unit)
    }

    fun getPasswordValidationErrors(password: String): Set<Constants.ValidationRule> {
        val errors = mutableSetOf<Constants.ValidationRule>()

        if (password.length < MIN_PASSWORD_LENGTH)
            errors.add(Constants.ValidationRule.PASSWORD_TOO_SHORT)

        if (!password.any { it.isUpperCase() })
            errors.add(Constants.ValidationRule.PASSWORD_NO_UPPERCASE)

        if (!password.any { it.isLowerCase() })
            errors.add(Constants.ValidationRule.PASSWORD_NO_LOWERCASE)

        if (!password.any { it.isDigit() })
            errors.add(Constants.ValidationRule.PASSWORD_NO_DIGIT)

        if (!SPECIAL_CHAR_REGEX.matches(password))
            errors.add(Constants.ValidationRule.PASSWORD_NO_SPECIAL_CHAR)

        return errors
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        private val SPECIAL_CHAR_REGEX = Regex(".*[!@#$%^&*()\\-_+={}\\[\\]|\\\\:;\"'<>,.?/].*")
    }
}