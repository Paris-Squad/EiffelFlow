package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.domain.model.exception.EiffelFlowException.PasswordValidationException
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.Validator
import org.junit.jupiter.api.Test

class ValidatePasswordUseCaseTest {

    private val useCase = ValidatePasswordUseCase(Validator)

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

        assertThat(result.exceptionOrNull()).isInstanceOf( PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no lowercase letters`() {
        val invalidPassword = "INVALID1PASSWORD!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf( PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no digits`() {
        val invalidPassword = "InvalidPassword!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf( PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password has no special characters`() {
        val invalidPassword = "Invalid1Password"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf( PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException when password is too short`() {
        val invalidPassword = "Inv1!"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf( PasswordValidationException::class.java)
    }

    @Test
    fun `validatePassword() should return PasswordValidationException with all errors when password fails multiple validations`() {
        val invalidPassword = "inv"

        val result = useCase.validatePassword(invalidPassword)

        assertThat(result.exceptionOrNull()).isInstanceOf( PasswordValidationException::class.java)
    }
}