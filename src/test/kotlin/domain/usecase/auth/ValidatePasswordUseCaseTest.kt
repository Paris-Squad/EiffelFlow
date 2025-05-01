package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.common.Constants
import org.example.domain.exception.EiffelFlowException.PasswordValidationException
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidatePasswordUseCaseTest {

    private val useCase = ValidatePasswordUseCase()

    @Test
    fun `validatePassword() should return success when password meets all requirements`() {
        val validPassword = "Valid1Password!"

        val result = useCase.validatePassword(validPassword)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no uppercase letters`() {
        val invalidPassword = "invalid1password!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf(PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no lowercase letters`() {
        val invalidPassword = "INVALID1PASSWORD!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf(PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no digits`() {
        val invalidPassword = "InvalidPassword!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf(PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no special characters`() {
        val invalidPassword = "Invalid1Password"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf(PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password is too short`() {
        val invalidPassword = "Inv1!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf(PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException with all errors when password fails multiple validations`() {
        val invalidPassword = "inv"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf(PasswordValidationException::class.java)
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

            assertThat(errors).contains(Constants.ValidationRule.PASSWORD_TOO_SHORT)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no uppercase letters`() {
            val invalidPassword = "valid1pass!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(Constants.ValidationRule.PASSWORD_NO_UPPERCASE)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no lowercase letters`() {
            val invalidPassword = "VALID1PASS!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(Constants.ValidationRule.PASSWORD_NO_LOWERCASE)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no digits`() {
            val invalidPassword = "ValidPass!"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(Constants.ValidationRule.PASSWORD_NO_DIGIT)
        }

        @Test
        fun `getPasswordValidationErrors() should return error when password has no special characters`() {
            val invalidPassword = "Valid1Pass"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).contains(Constants.ValidationRule.PASSWORD_NO_SPECIAL_CHAR)
        }

        @Test
        fun `getPasswordValidationErrors() should return multiple errors for multiple violations`() {
            val invalidPassword = "pass"

            val errors = useCase.getPasswordValidationErrors(invalidPassword)

            assertThat(errors).hasSize(4)
        }
    }
}