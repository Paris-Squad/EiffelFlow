package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.common.Constants
import org.example.domain.exception.EiffelFlowException.UserNameValidationException
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidateUserNameUseCaseTest {

    private val useCase = ValidateUserNameUseCase()

    @Test
    fun `validateUserName() should return success when username meets all requirements`() {
        val validUserName = "valid_username123"

        val result = useCase.validateUserName(validUserName)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `validateUserName() should return UserNameValidationException when username is too short`() {
        val invalidUserName = "us"

        val result = useCase.validateUserName(invalidUserName)

        assertThat(result.exceptionOrNull()).isInstanceOf(UserNameValidationException::class.java)
       }

    @Test
    fun `validateUserName() should return UserNameValidationException when username is too long`() {
        val invalidUserName = "thisusernameiswaytoolongandexceedsthethirtycharacterlimit"

        val result = useCase.validateUserName(invalidUserName)

        assertThat(result.exceptionOrNull()).isInstanceOf(UserNameValidationException::class.java)
    }

    @Test
    fun `validateUserName() should return UserNameValidationException when username contains invalid characters`() {
        val invalidUserName = "invalid@username!"

        val result = useCase.validateUserName(invalidUserName)

        assertThat(result.exceptionOrNull()).isInstanceOf(UserNameValidationException::class.java)
    }

    @Test
    fun `validateUserName() should return UserNameValidationException with all errors when username fails multiple validations`() {
        val invalidUserName = "@"

        val result = useCase.validateUserName(invalidUserName)

        assertThat(result.exceptionOrNull()).isInstanceOf(UserNameValidationException::class.java)
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

            assertThat(errors).contains(Constants.ValidationRule.USERNAME_TOO_SHORT)
        }

        @Test
        fun `validateUserNameDetails() should return error when username is too long`() {
            val invalidUsername = "a".repeat(31)

            val errors = useCase.getUserNameValidationErrors(invalidUsername)

            assertThat(errors).contains(Constants.ValidationRule.USERNAME_TOO_LONG)
        }

        @Test
        fun `validateUserNameDetails() should return error when username contains invalid characters`() {
            val invalidUserNames = listOf("user@name", "user-name", "user.name", "user name", "user#123", "user$")

            invalidUserNames.forEach { username ->
                val errors = useCase.getUserNameValidationErrors(username)
                assertThat(errors).contains(Constants.ValidationRule.USERNAME_INVALID_CHARACTERS)
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