package org.example.domain.usecase.auth

import org.example.domain.exception.EiffelFlowException.AuthenticationException
import org.example.domain.utils.ValidationErrorMessage

class ValidatePasswordUseCase {
    fun validatePassword(password: String) {
        val validationResult = getPasswordValidationErrors(password)

         if (validationResult.isNotEmpty()) {
           throw AuthenticationException(validationResult)
        }
    }

    fun getPasswordValidationErrors(password: String): Set<ValidationErrorMessage> {
        val errors = mutableSetOf<ValidationErrorMessage>()

        if (password.length < MIN_PASSWORD_LENGTH)
            errors.add(ValidationErrorMessage.PASSWORD_TOO_SHORT)

        if (!password.any { it.isUpperCase() })
            errors.add(ValidationErrorMessage.PASSWORD_NO_UPPERCASE)

        if (!password.any { it.isLowerCase() })
            errors.add(ValidationErrorMessage.PASSWORD_NO_LOWERCASE)

        if (!password.any { it.isDigit() })
            errors.add(ValidationErrorMessage.PASSWORD_NO_DIGIT)

        if (!SPECIAL_CHAR_REGEX.matches(password))
            errors.add(ValidationErrorMessage.PASSWORD_NO_SPECIAL_CHAR)

        return errors
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        private val SPECIAL_CHAR_REGEX = Regex(".*[!@#$%^&*()\\-_+={}\\[\\]|\\\\:;\"'<>,.?/].*")
    }
}