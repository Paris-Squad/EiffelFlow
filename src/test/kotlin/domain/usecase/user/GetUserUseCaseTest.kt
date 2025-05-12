package domain.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.user.GetUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock

class GetUserUseCaseTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private lateinit var getUserUseCase: GetUserUseCase
    private val sessionManger: SessionManger = mockk(relaxed = true)


    @BeforeEach
    fun setup() {
        getUserUseCase = GetUserUseCase(userRepository)
    }

    @Test
    fun `getUsers should return list of users when users exist `() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            coEvery { userRepository.getUsers() } returns listOf(UserMock.validUser)

            // When
            val result = getUserUseCase.getUsers()

            // Then
            assertThat(result).containsExactlyElementsIn(listOf(UserMock.validUser))
        }
    }


    @Test
    fun `getUserById should return user when user is admin`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            coEvery { userRepository.getUserById(UserMock.validUser.userId) } returns UserMock.validUser

            // When
            val result = getUserUseCase.getUserById(UserMock.validUser.userId)

            // Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }
    }

}