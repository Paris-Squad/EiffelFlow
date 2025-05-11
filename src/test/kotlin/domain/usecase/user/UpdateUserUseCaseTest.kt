package domain.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase
import org.example.domain.usecase.user.UpdateUserUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock

class UpdateUserUseCaseTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val hashPasswordUseCase: HashPasswordUseCase = mockk(relaxed = true)
    private lateinit var updateUserUseCase: UpdateUserUseCase
    private val auditRepository: AuditRepository = mockk(relaxed = true)


    @BeforeEach
    fun setUp() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
        every { SessionManger.isLoggedIn() } returns true
        updateUserUseCase = UpdateUserUseCase(
            userRepository = userRepository,
            hashPasswordUseCase = hashPasswordUseCase,
            auditRepository = auditRepository
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SessionManger)
    }

    @Test
    fun `update user should return the updated user on success`() {
        runTest {
            // Given
            coEvery {
                userRepository.updateUser(any())
            } returns UserMock.adminUser

            // When
            val result = updateUserUseCase.updateUser(
                userName = UserMock.adminUser.username,
                currentPassword = UserMock.adminUser.password,
                newPassword = "Updated Password"
            )

            // Then
            assertThat(result).isEqualTo(UserMock.adminUser)
        }
    }

    @Test
    fun `update user should fail when repository createUser fails`() {
        runTest {
            // Given
            coEvery { SessionManger.getUser() } returns UserMock.adminUser
            coEvery {
                userRepository.updateUser(any())
            } throws EiffelFlowException.IOException("User deletion failed")

            // When / Then
            val result = assertThrows<EiffelFlowException.IOException> {
                updateUserUseCase.updateUser(
                    userName = UserMock.adminUser.username,
                    currentPassword = UserMock.adminUser.password,
                    newPassword = "Updated Password"
                )
            }
            assertThat(result.message).contains("User deletion failed")
        }
    }

    @Test
    fun `update user should fail when user is not logged in`() {
        runTest {
            // Given
            every {
                SessionManger.isLoggedIn()
            } returns false

            coEvery {
                userRepository.updateUser(any())
            } returns UserMock.validUser
            // When / Then
            val result = assertThrows<EiffelFlowException.AuthorizationException> {
                updateUserUseCase.updateUser(
                    userName = UserMock.validUser.username,
                    currentPassword = UserMock.validUser.password,
                    newPassword = "Updated Password"
                )
            }
            assertThat(result.message).isEqualTo("You must be logged in to update user")
        }
    }

    @Test
    fun `update user should fail when current password entered by the user not matching the current password in the records`() {
        runTest {
            // Given
            coEvery {
                userRepository.updateUser(any())
            } returns UserMock.validUser
            // When / Then
            val result = assertThrows<EiffelFlowException.AuthorizationException> {
                updateUserUseCase.updateUser(
                    userName = UserMock.validUser.username,
                    currentPassword = "UserMock.validUser.password",
                    newPassword = "Updated Password"
                )
            }
            assertThat(result.message).isEqualTo("Current password is not correct")
        }
    }
}