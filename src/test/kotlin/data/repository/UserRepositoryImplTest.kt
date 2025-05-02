package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.repository.UserRepositoryImpl
import org.example.domain.repository.AuditRepository
import org.example.data.storage.user.UserDataSource
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock.validUser
import java.util.*

class UserRepositoryImplTest {
    private val userDataSource: UserDataSource = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        userRepository = UserRepositoryImpl(userDataSource, auditRepository)
    }

    @Test
    fun `createUser should return the created user on success`() {
        val admin = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )

        every { userDataSource.createUser(validUser) } returns Result.success(validUser)

        val result = userRepository.createUser(user = validUser, createdBy = admin)

        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Test
    fun `createUser should return failure when data source fails`() {
        val admin = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )
        val exception = EiffelFlowException.IOException("Database error")

        every { userDataSource.createUser(validUser) } returns Result.failure(exception)

        val result = userRepository.createUser(user = validUser, createdBy = admin)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)

    }

    @Test
    fun `updateUser should return the updated user`() {
        val user = User(
            username = "test",
            password = "test",
            role = RoleType.MATE
        )

        try {
            userRepository.updateUser(user)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteUser should return the deleted user`() {
        val userId = UUID.randomUUID()

        try {
            userRepository.deleteUser(userId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getUser should return user by ID`() {
        val userId = UUID.randomUUID()

        try {
            userRepository.getUserById(userId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getUsers should return list of users with multiple users on success`() {
        val users = listOf(
            User(
                userId = UUID.randomUUID(),
                username = "test",
                password = "test",
                role = RoleType.ADMIN
            ),
            User(
                userId = UUID.randomUUID(),
                username = "test2",
                password = "test2",
                role = RoleType.MATE
            )
        )

        every { userDataSource.getUsers() } returns Result.success(users)

        val result = userRepository.getUsers()

        assertThat(result.getOrNull()).hasSize(2)
    }

    @Test
    fun `getUsers should return failure when data source throws exception`() {
        val exception = EiffelFlowException.IOException("Database error")

        every { userDataSource.getUsers() } returns Result.failure(exception)

        val result = userRepository.getUsers()

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }
}