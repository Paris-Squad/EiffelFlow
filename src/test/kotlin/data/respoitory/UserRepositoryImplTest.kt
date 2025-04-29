package data.respoitory

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.respoitory.UserRepositoryImpl
import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.user.UserDataSource
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import org.example.domain.model.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class UserRepositoryImplTest {
    private val userDataSource: UserDataSource = mockk(relaxed = true)
    private val auditDataSource: AuditDataSource = mockk(relaxed = true)
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        userRepository = UserRepositoryImpl(userDataSource, auditDataSource)
    }

    @Test
    fun `createUser should return the created user on success`() {
        val user = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )

        every { userDataSource.createUser(user) } returns Result.success(user)

        val result = userRepository.createUser(user)

        assertThat(result.getOrNull()).isEqualTo(user)
    }

    @Test
    fun `createUser should return failure when data source fails`() {
        val user = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )
        val exception = EiffelFlowException.UserCreationException("Database error")

        every { userDataSource.createUser(user) } returns Result.failure(exception)

        val result = userRepository.createUser(user)

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
        val exception = EiffelFlowException.UserStorageException("Database error")

        every { userDataSource.getUsers() } returns Result.failure(exception)

        val result = userRepository.getUsers()

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }
}