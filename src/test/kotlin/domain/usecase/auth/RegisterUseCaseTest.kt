package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase
import org.example.domain.usecase.user.CreateUserUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock.adminUser

class RegisterUseCaseTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val hashPasswordUseCase: HashPasswordUseCase = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)


    private lateinit var registerUseCase: CreateUserUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns adminUser
        every { SessionManger.isAdmin() } returns true
        registerUseCase = CreateUserUseCase(
            userRepository, hashPasswordUseCase,
            auditRepository = auditRepository
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SessionManger)
    }

    @Test
    fun `register with admin role should succeed when all validations pass`() {
        runTest {
            // Given
            val createdUser = User(username = username, password = hashedPassword, role = mateRole)
            coEvery { userRepository.getUsers() } returns emptyList()
            every { hashPasswordUseCase.hashPassword(password) } returns hashedPassword
            coEvery { userRepository.createUser(any()) } returns createdUser

            // When
            val result = registerUseCase.register(username, password, mateRole)

            // Then
            assertEquals(createdUser, result)
        }
    }

    @Test
    fun `register should successfully create user when all validations pass`() {
        runTest {
            // Given
            val createdUser = User(username = username, password = hashedPassword, role = mateRole)
            coEvery { userRepository.getUsers() } returns emptyList()
            every { hashPasswordUseCase.hashPassword(password) } returns hashedPassword
            coEvery { userRepository.createUser(any()) } returns createdUser

            // When
            val result = registerUseCase.register(username, password, mateRole)

            // Then
            assertEquals(createdUser, result)
        }
    }

    @Test
    fun `register should fail when repository createUser fails`() {
        runTest {
            // Given
            val repositoryException = EiffelFlowException.IOException("User creation failed")
            coEvery { userRepository.getUsers() } returns emptyList()
            every { hashPasswordUseCase.hashPassword(any()) } returns "hashedPassword"
            coEvery { userRepository.createUser(any()) } throws repositoryException

            // When / Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                registerUseCase.register(username, password, mateRole)
            }
            assertThat(exception.message).contains("User creation failed")
        }
    }

    companion object {
        private const val username = "testuser"
        private const val password = "P@ssw0rd"
        private const val hashedPassword = "hashedP@ssw0rd"
        private val mateRole = RoleType.MATE
    }

}