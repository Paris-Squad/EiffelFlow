package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.common.Constants
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock.adminUser
import utils.UserMock.validUser
import java.io.FileNotFoundException

class RegisterUseCaseTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val validatePasswordUseCase: ValidatePasswordUseCase = mockk(relaxed = true)
    private val validateUsernameUseCase: ValidateUserNameUseCase = mockk(relaxed = true)
    private val hashPasswordUseCase: HashPasswordUseCase = mockk(relaxed = true)

    private lateinit var registerUseCase: RegisterUseCase

    @BeforeEach
    fun setUp() {

        registerUseCase = RegisterUseCase(
            userRepository, validatePasswordUseCase, validateUsernameUseCase, hashPasswordUseCase
        )

        every { validateUsernameUseCase.validateUserName(any()) } returns Result.success(Unit)
        every { validatePasswordUseCase.validatePassword(any()) } returns Result.success(Unit)
    }

    @Test
    fun `register should fail when repository getUsers fails`() {
        val repositoryException = FileNotFoundException("Failed to retrieve users")

        every { userRepository.getUsers() } returns Result.failure(repositoryException)

        val result = registerUseCase.register(username, password, mateRole, adminUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(repositoryException::class.java)
    }

    @Test
    fun `register with non-admin caller role should fail with unauthorized exception`() {
        val result = registerUseCase.register(username, password, mateRole, validUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.AuthorizationException::class.java)
    }

    @Test
    fun `register with admin caller role should succeed when all validations pass`() {
        val createdUser = User(username = username, password = hashedPassword, role = mateRole)

        every { userRepository.getUsers() } returns Result.success(emptyList())
        every { hashPasswordUseCase.hashPassword(password) } returns hashedPassword
        every { userRepository.createUser(any() , any()) } returns Result.success(createdUser)

        val result = registerUseCase.register(username, password, mateRole, adminUser)

        assertEquals(createdUser, result.getOrNull())
    }

    @Test
    fun `register should successfully create user when all validations pass`() {
        val createdUser = User(username = username, password = hashedPassword, role = mateRole)

        every { userRepository.getUsers() } returns Result.success(emptyList())
        every { hashPasswordUseCase.hashPassword(password) } returns hashedPassword
        every { userRepository.createUser(any() , any()) } returns Result.success(createdUser)

        val result = registerUseCase.register(username, password, mateRole, adminUser)

        assertEquals(createdUser, result.getOrNull())
    }

    @Test
    fun `register should fail when username validation fails`() {
        val validationException =
            EiffelFlowException.AuthenticationException(setOf(Constants.ValidationRule.USERNAME_TOO_LONG))

        every { validateUsernameUseCase.validateUserName(username) } returns Result.failure(validationException)

        val result = registerUseCase.register(username, password, mateRole, adminUser)

        assertTrue(result.isFailure)
        assertEquals(validationException, result.exceptionOrNull())

        verify { validateUsernameUseCase.validateUserName(username) }
    }

    @Test
    fun `register should fail when password validation fails`() {
        val validationException = EiffelFlowException.AuthenticationException(setOf(Constants.ValidationRule.PASSWORD_TOO_SHORT))

        every { validatePasswordUseCase.validatePassword(password) } returns Result.failure(validationException)

        val result = registerUseCase.register(username, password, mateRole, adminUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(validationException::class.java)
    }

    @Test
    fun `register should fail when username already exists`() {
        val existingUsers = listOf(User(username = username, password = "otherpass", role = RoleType.MATE))

        every { userRepository.getUsers() } returns Result.success(existingUsers)

        val result = registerUseCase.register(username, password, mateRole, adminUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.AuthorizationException::class.java)
    }

    @Test
    fun `register should fail when repository createUser fails`() {
        val repositoryException = FileNotFoundException("User creation failed")

        every { userRepository.getUsers() } returns Result.success(emptyList())
        every { hashPasswordUseCase.hashPassword(any()) } returns "hashedPassword"
        every { userRepository.createUser(any(),any()) } returns Result.failure(repositoryException)

        val result = registerUseCase.register(username, password, mateRole , adminUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(repositoryException::class.java)
    }

   

    companion object {
        private const val username = "testuser"
        private const val password = "P@ssw0rd"
        private const val hashedPassword = "hashedP@ssw0rd"
        private val mateRole = RoleType.MATE
    }

}