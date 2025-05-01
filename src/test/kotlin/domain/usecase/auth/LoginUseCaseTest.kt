package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.common.Constants
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.LoginUseCase
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock



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
        every { validateUsernameUseCase.validateUserName(UserMock.validUser.username) } returns Result.success(Unit)
        every { validatePasswordUseCase.validatePassword(UserMock.validUser.password) } returns Result.success(Unit)
        every { userRepository.getUsers() } returns Result.success(UserMock.userList)

        // When
        val result = loginUseCase.login(UserMock.validUser.username, UserMock.validUser.password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Login successfully", result.getOrNull())
    }
    @Test
    fun `login should return failure when password are incorrect`(){
        //Given
            every { userRepository.getUsers() } returns Result.success(listOf(UserMock.validUser))
        //When
            val result = loginUseCase.login(UserMock.validUser.username, UserMock.invalidUser.password)
        //Then
            assertTrue(result.isFailure)
            assertEquals("Password validation failed: Invalid password", result.exceptionOrNull()?.message)
    }
    @Test
    fun `login should return failure when userName are incorrect`(){
        //Given
        every { userRepository.getUsers() } returns Result.success(listOf(UserMock.validUser))
        //When
        val result = loginUseCase.login("wrong username", UserMock.validUser.password)
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
            EiffelFlowException.UserNameValidationException(setOf(Constants.ValidationRule.USERNAME_TOO_LONG))

        every { validateUsernameUseCase.validateUserName(UserMock.invalidUser.username) } returns Result.failure(validationException)

        val result = loginUseCase.login(UserMock.invalidUser.username, UserMock.validUser.password)

        assertTrue(result.isFailure)
        assertEquals(validationException, result.exceptionOrNull())

        verify { validateUsernameUseCase.validateUserName(UserMock.invalidUser.username) }
    }
    @Test
    fun `register should fail when password validation fails`() {
        val validationException = EiffelFlowException.PasswordValidationException(setOf(Constants.ValidationRule.PASSWORD_TOO_SHORT))

        every { validatePasswordUseCase.validatePassword(UserMock.invalidUser.password) } returns Result.failure(validationException)

        val result = loginUseCase.login(UserMock.validUser.username, UserMock.invalidUser.password)

        assertThat(result.exceptionOrNull()).isInstanceOf(validationException::class.java)
    }
}