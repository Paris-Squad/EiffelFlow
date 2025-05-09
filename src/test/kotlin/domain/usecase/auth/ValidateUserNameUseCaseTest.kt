package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.domain.utils.ValidationErrorMessage
import org.example.domain.exception.EiffelFlowException.AuthenticationException
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidateUserNameUseCaseTest {

    private val useCase = ValidateUserNameUseCase()

    @Test
    fun `validateUserName() should return success when username meets all requirements`() {
        val validUserName = "valid_username123"

         useCase.validateUserName(validUserName)
    }

    @Test
    fun `validateUserName() should return AuthenticationException when username is too short`() {
        val invalidUserName = "us"

        val exception = assertThrows<AuthenticationException> {
            useCase.validateUserName(invalidUserName)
        }

        assertThat(exception.message).contains("Username must be at least 3 characters long")
       }

    @Test
    fun `validateUserName() should return AuthenticationException when username is too long`() {
        val invalidUserName = "thisusernameiswaytoolongandexceedsthethirtycharacterlimit"

        val exception = assertThrows<AuthenticationException> {
            useCase.validateUserName(invalidUserName)
        }

        assertThat(exception.message).contains("Username must be at most 30 characters long")
    }

    @Test
    fun `validateUserName() should return AuthenticationException when username contains invalid characters`() {
        val invalidUserName = "invalid@username!"

        val exception = assertThrows<AuthenticationException> {
            useCase.validateUserName(invalidUserName)
        }

        assertThat(exception.message).contains("Username must contain only letters, numbers, and underscores")
    }

    @Test
    fun `validateUserName() should return AuthenticationException with all errors when username fails multiple validations`() {
        val invalidUserName = "@"

        val exception = assertThrows<AuthenticationException> {
            useCase.validateUserName(invalidUserName)
        }

        assertThat(exception.message).contains("Username must contain only letters, numbers, and underscores")
    }

    @Nested
    inner class UserNameValidationTests {

        @Test
        fun `validateUserNameDetails() should return empty set when username meets all requirements`() {
            val validUserNames = listOf("user", "user123", "user_name", "USER", "User_123")

            validUserNames.forEach { username ->
                val errors = useCase.getUserNameValidationErrors(username)
                assertThat(errors).isEmpty()
            }
        }

        @Test
        fun `validateUserNameDetails() should return error when username is too short`() {
            val invalidUsername = "us"

            val errors = useCase.getUserNameValidationErrors(invalidUsername)

            assertThat(errors).contains(ValidationErrorMessage.USERNAME_TOO_SHORT)
        }

        @Test
        fun `validateUserNameDetails() should return error when username is too long`() {
            val invalidUsername = "a".repeat(31)

            val errors = useCase.getUserNameValidationErrors(invalidUsername)

            assertThat(errors).contains(ValidationErrorMessage.USERNAME_TOO_LONG)
        }

        @Test
        fun `validateUserNameDetails() should return error when username contains invalid characters`() {
            val invalidUserNames = listOf("user@name", "user-name", "user.name", "user name", "user#123", "user$")

            invalidUserNames.forEach { username ->
                val errors = useCase.getUserNameValidationErrors(username)
                assertThat(errors).contains(ValidationErrorMessage.USERNAME_INVALID_CHARACTERS)
            }
        }

        @Test
        fun `validateUserNameDetails() should return multiple errors for multiple violations`() {
            val invalidUsername = "@"

            val errors = useCase.getUserNameValidationErrors(invalidUsername)

            assertThat(errors).hasSize(2)
        }
    }
}