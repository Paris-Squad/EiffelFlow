package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import org.example.domain.model.exception.EiffelFlowException.UserNameValidationException
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.example.domain.usecase.auth.Validator
import org.junit.jupiter.api.Test

class ValidateUserNameUseCaseTest {

    private val useCase = ValidateUserNameUseCase(Validator)

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
}