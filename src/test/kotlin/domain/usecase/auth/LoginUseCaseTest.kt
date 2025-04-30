package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.common.ValidationMessages
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.LoginUseCase
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockUser



class LoginUseCaseTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val validatePasswordUseCase: ValidatePasswordUseCase = mockk(relaxed = true)
    private val validateUsernameUseCase: ValidateUserNameUseCase = mockk(relaxed = true)
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setup(){
        loginUseCase = LoginUseCase(userRepository, validatePasswordUseCase, validateUsernameUseCase)
    }

    @Test
    fun `login should return success when credentials are correct`() {
        // Given
        every { validateUsernameUseCase.validateUserName(MockUser.validUser.username) } returns Result.success(Unit)
        every { validatePasswordUseCase.validatePassword(MockUser.validUser.password) } returns Result.success(Unit)
        every { userRepository.getUsers() } returns Result.success(MockUser.userList)

        // When
        val result = loginUseCase.login(MockUser.validUser.username, MockUser.validUser.password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Login successfully", result.getOrNull())
    }
    @Test
    fun `login should return failure when password are incorrect`(){
        //Given
            every { userRepository.getUsers() } returns Result.success(listOf(MockUser.validUser))
        //When
            val result = loginUseCase.login(MockUser.validUser.username, MockUser.invalidUser.password)
        //Then
            assertTrue(result.isFailure)
            assertEquals("Password validation failed: Invalid password", result.exceptionOrNull()?.message)
    }
    @Test
    fun `login should return failure when userName are incorrect`(){
        //Given
        every { userRepository.getUsers() } returns Result.success(listOf(MockUser.validUser))
        //When
        val result = loginUseCase.login("wrong username", MockUser.validUser.password)
        //Then
        assertTrue(result.isFailure)
        assertEquals("Username validation failed: Invalid userName", result.exceptionOrNull()?.message)
    }
    @Test
    fun `login should return failure when userRepository returns failure`() {
        // Given
        val exception = RuntimeException("Data source error")
        every { userRepository.getUsers() } returns Result.failure(exception)

        // When
        val result = loginUseCase.login("validUser", "validPass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Data source error", result.exceptionOrNull()?.message)
    }
    @Test
    fun `register should fail when username validation fails`(){
        val validationException =
            EiffelFlowException.UserNameValidationException(setOf(ValidationMessages.ValidationRule.USERNAME_TOO_LONG))

        every { validateUsernameUseCase.validateUserName(MockUser.invalidUser.username) } returns Result.failure(validationException)

        val result = loginUseCase.login(MockUser.invalidUser.username, MockUser.validUser.password)

        assertTrue(result.isFailure)
        assertEquals(validationException, result.exceptionOrNull())

        verify { validateUsernameUseCase.validateUserName(MockUser.invalidUser.username) }
    }
    @Test
    fun `register should fail when password validation fails`() {
        val validationException = EiffelFlowException.PasswordValidationException(setOf(ValidationMessages.ValidationRule.PASSWORD_TOO_SHORT))

        every { validatePasswordUseCase.validatePassword(MockUser.invalidUser.password) } returns Result.failure(validationException)

        val result = loginUseCase.login(MockUser.validUser.username, MockUser.invalidUser.password)

        assertThat(result.exceptionOrNull()).isInstanceOf(validationException::class.java)
    }
}