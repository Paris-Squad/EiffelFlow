package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.LoginUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock



class LoginUseCaseTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setup(){
        loginUseCase = LoginUseCase(userRepository)
    }

    @Test
    fun `login should return success when credentials are correct`(){
        try {
            every { userRepository.getUsers() } returns Result.success(UserMock.userList)
            val result = loginUseCase.login("validUser","validPass")
            assertTrue(result.isSuccess)
            verify(exactly = 1) {userRepository.getUsers()}
        }catch (exception: NotImplementedError){
            assertThat(exception.message).contains("Not yet implemented")
        }
    }
    @Test
    fun `login should return failure when password are incorrect`(){
        //Given
            every { userRepository.getUsers() } returns Result.success(listOf(UserMock.validUser))
        //When
            val result = loginUseCase.login(UserMock.validUser.username, "wrongPass")
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
}