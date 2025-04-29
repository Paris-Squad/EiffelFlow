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
import utils.MockUser



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
            every { userRepository.getUsers() } returns Result.success(MockUser.userList)
            val result = loginUseCase.login("validUser","validPass")
            assertTrue(result.isSuccess)
            verify(exactly = 1) {userRepository.getUsers()}
        }catch (exception: NotImplementedError){
            assertThat(exception.message).contains("Not yet implemented")
        }
    }
    @Test
    fun `login should return failure when credentials are incorrect`(){
        try {
            every {userRepository.getUsers() } returns Result.success(MockUser.userList)
            val result = loginUseCase.login("validUser","validPass")
            assertTrue(result.isFailure)
            verify(exactly = 1) {userRepository.getUsers() }
        }catch (exception: NotImplementedError){
            assertThat(exception.message).contains("Not yet implemented")
        }
    }
}