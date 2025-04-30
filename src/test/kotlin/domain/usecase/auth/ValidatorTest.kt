package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.domain.usecase.auth.Validator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidatorTest {

    @Nested
    inner class PasswordValidationTests {

        @Test
        fun `validatePassword() should return empty set when password meets all requirements`() {
            val validPassword = "Valid1Pass!"

            val errors = Validator.validatePassword(validPassword)

            assertThat(errors).isEmpty()
        }

        @Test
        fun `validatePassword() should return error when password is too short`() {
            val invalidPassword = "Abc123!"

            val errors = Validator.validatePassword(invalidPassword)

            assertThat(errors).contains("Password must be at least 8 characters long")
        }

        @Test
        fun `validatePassword() should return error when password has no uppercase letters`() {
            val invalidPassword = "valid1pass!"

            val errors = Validator.validatePassword(invalidPassword)

            assertThat(errors).contains("Password must contain at least one uppercase letter")
        }

        @Test
        fun `validatePassword() should return error when password has no lowercase letters`() {
            val invalidPassword = "VALID1PASS!"

            val errors = Validator.validatePassword(invalidPassword)

            assertThat(errors).contains("Password must contain at least one lowercase letter")
        }

        @Test
        fun `validatePassword() should return error when password has no digits`() {
            val invalidPassword = "ValidPass!"

            val errors = Validator.validatePassword(invalidPassword)

            assertThat(errors).contains("Password must contain at least one digit")
        }

        @Test
        fun `validatePassword() should return error when password has no special characters`() {
            val invalidPassword = "Valid1Pass"

            val errors = Validator.validatePassword(invalidPassword)

            assertThat(errors).contains("Password must contain at least one special character")
        }

        @Test
        fun `validatePassword() should return multiple errors for multiple violations`() {
            val invalidPassword = "pass"

            val errors = Validator.validatePassword(invalidPassword)

            assertThat(errors).hasSize(4)
        }
    }

    @Nested
    inner class UserNameValidationTests {

        @Test
        fun `validateUserName() should return empty set when username meets all requirements`() {
            val validUserNames = listOf("user", "user123", "user_name", "USER", "User_123")

            validUserNames.forEach { username ->
                val errors = Validator.validateUserName(username)
                assertThat(errors).isEmpty()
            }
        }

        @Test
        fun `validateUserName() should return error when username is too short`() {
            val invalidUsername = "us"

            val errors = Validator.validateUserName(invalidUsername)

            assertThat(errors).contains("Username must be at least 3 characters long")
        }

        @Test
        fun `validateUserName() should return error when username is too long`() {
            val invalidUsername = "a".repeat(31)

            val errors = Validator.validateUserName(invalidUsername)

            assertThat(errors).contains("Username must be at most 30 characters long")
        }

        @Test
        fun `validateUserName() should return error when username contains invalid characters`() {
            val invalidUserNames = listOf("user@name", "user-name", "user.name", "user name", "user#123", "user$")

            invalidUserNames.forEach { username ->
                val errors = Validator.validateUserName(username)
                assertThat(errors).contains("Username must contain only letters, numbers, and underscores")
            }
        }

        @Test
        fun `validateUserName() should return multiple errors for multiple violations`() {
            val invalidUsername = "@"

            val errors = Validator.validateUserName(invalidUsername)

            assertThat(errors).hasSize(2)
            assertThat(errors).contains("Username must be at least 3 characters long")
            assertThat(errors).contains("Username must contain only letters, numbers, and underscores")
        }
    }
}