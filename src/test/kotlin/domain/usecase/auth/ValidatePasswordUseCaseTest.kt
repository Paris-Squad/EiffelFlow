package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.domain.utils.ValidationErrorMessage
import org.example.domain.exception.EiffelFlowException.AuthenticationException
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidatePasswordUseCaseTest {

    private val useCase = ValidatePasswordUseCase()

    @Test
    fun `validatePassword() should return success when password meets all requirements`() {
        val validPassword = "Valid1Password!"

        useCase.validatePassword(validPassword)

    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no uppercase letters`() {
        val invalidPassword = "invalid1password!"

        val exception = assertThrows<AuthenticationException> {
            useCase.validatePassword(invalidPassword)
        }

        assertThat(exception.message).contains("Password must contain at least one uppercase letter")
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no lowercase letters`() {
        val invalidPassword = "INVALID1PASSWORD!"

        val exception = assertThrows<AuthenticationException> {
            useCase.validatePassword(invalidPassword)
        }

        assertThat(exception.message).contains("Password must contain at least one lowercase letter")
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no digits`() {
        val invalidPassword = "InvalidPassword!"

        val exception = assertThrows<AuthenticationException> {
            useCase.validatePassword(invalidPassword)
        }

        assertThat(exception.message).contains("Password must contain at least one digit")
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no special characters`() {
        val invalidPassword = "Invalid1Password"

        val exception = assertThrows<AuthenticationException> {
            useCase.validatePassword(invalidPassword)
        }

        assertThat(exception.message).contains("Password must contain at least one special character")
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password is too short`() {
        val invalidPassword = "Inv1!"

        val exception = assertThrows<AuthenticationException> {
            useCase.validatePassword(invalidPassword)
        }

        assertThat(exception.message).contains("Password must be at least 8 characters long")
    }

    @Test
    fun `validatePassword() should return PasswordValidationException with all errors when password fails multiple validations`() {
        val invalidPassword = "inv"

        val exception = assertThrows<AuthenticationException> {
            useCase.validatePassword(invalidPassword)
        }

        assertThat(exception.message).contains("Password must be at least 8 characters long")
    }

    @Nested
    inner class PasswordValidationTests {

        @Test
        fun `getPasswordValidationErrors() should return empty set when password meets all requirements`() {
            val validPassword = "Valid1Pass!"

            val errors = useCase.getPasswordValidationErrors(validPassword)

            assertThat(errors).isEmpty()
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password is too short`() {
            val invalidPassword = "Abc123!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(ValidationErrorMessage.PASSWORD_TOO_SHORT)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no uppercase letters`() {
            val invalidPassword = "valid1pass!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(ValidationErrorMessage.PASSWORD_NO_UPPERCASE)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no lowercase letters`() {
            val invalidPassword = "VALID1PASS!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(ValidationErrorMessage.PASSWORD_NO_LOWERCASE)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no digits`() {
            val invalidPassword = "ValidPass!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(ValidationErrorMessage.PASSWORD_NO_DIGIT)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no special characters`() {
            val invalidPassword = "Valid1Pass"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(ValidationErrorMessage.PASSWORD_NO_SPECIAL_CHAR)
        }

        @Test
        fun `getPasswordValidationErrors() should return multiple errors for multiple violations`() {
            val invalidPassword = "pass"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).hasSize(4)
        }
    }
}