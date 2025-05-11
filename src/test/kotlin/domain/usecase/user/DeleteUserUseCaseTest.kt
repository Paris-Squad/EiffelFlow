package domain.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.user.DeleteUserUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock

class DeleteUserUseCaseTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private lateinit var deleteUserUseCase: DeleteUserUseCase
    private val auditRepository: AuditRepository = mockk(relaxed = true)


    @BeforeEach
    fun setUp() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
        every { SessionManger.isAdmin() } returns true
        deleteUserUseCase = DeleteUserUseCase(userRepository = userRepository, auditRepository = auditRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SessionManger)
    }

    @Test
    fun `delete user should return the deleted user on success`() {
        runTest {
            // Given
            coEvery {
                userRepository.deleteUser(any())
            } returns UserMock.validUser

            // When
            val result = deleteUserUseCase.deleteUser(UserMock.validUser.userId)

            // Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }
    }

    @Test
    fun `delete user should fail when user is not Admin`() {
        runTest {
            // Given
            every { SessionManger.getUser() } returns UserMock.validUser
            every { SessionManger.isAdmin() } returns false

            // When / Then
            val result = assertThrows<EiffelFlowException.AuthorizationException> {
                deleteUserUseCase.deleteUser(UserMock.validUser.userId)
            }
            assertThat(result.message).isEqualTo("Only admins can delete users")
        }
    }

    @Test
    fun `delete user should fail when repository createUser fails`() {
        runTest {
            // Given
            coEvery { SessionManger.getUser() } returns UserMock.adminUser
            coEvery {
                userRepository.deleteUser(any())
            } throws EiffelFlowException.IOException("User deletion failed")

            // When / Then
            val result = assertThrows<EiffelFlowException.IOException> {
                deleteUserUseCase.deleteUser(UserMock.validUser.userId)
            }
            assertThat(result.message).contains("User deletion failed")
        }
    }

    @Test
    fun `delete user should fail when user is not logged in`() {
        runTest {
            // Given
            every {
                SessionManger.isAdmin()
            } throws EiffelFlowException.AuthorizationException("User is not logged in")

            coEvery {
                userRepository.deleteUser(any())
            } returns UserMock.validUser
            // When / Then
            val result = assertThrows<EiffelFlowException.AuthorizationException> {
                deleteUserUseCase.deleteUser(UserMock.validUser.userId)
            }
            assertThat(result.message).isEqualTo("User is not logged in")
        }
    }

}