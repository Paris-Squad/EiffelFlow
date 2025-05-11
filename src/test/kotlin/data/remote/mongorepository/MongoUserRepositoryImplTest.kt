package data.remote.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.MongoUserRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoUserDto
import org.example.data.remote.mapper.UserMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock
import java.util.*

class MongoUserRepositoryImplTest {

    private val sessionManger: SessionManger = mockk(relaxed = true)
    private val userMapper: UserMapper = mockk(relaxed = true)
    private lateinit var usersCollection: MongoCollection<MongoUserDto>
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        usersCollection = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every {
            mockDatabase.getCollection<MongoUserDto>(MongoCollections.USERS)
        } returns usersCollection
        every { sessionManger.getUser() } returns UserMock.adminUser
        userRepository = MongoUserRepositoryImpl(
            database = mockDatabase,
            userMapper = userMapper
        )
    }

    //region createUser
    @Test
    fun `createUser should insert user if not exists`() = runTest {
        //Given
        coEvery {
            usersCollection.insertOne(any())
        } returns mockk()

        //When
        val result = userRepository.createUser(UserMock.validUser)

        //Then
        assertThat(result).isEqualTo(UserMock.validUser)
    }

    @Test
    fun `createUser should throw Exception when user is not admin`() = runTest {
        // Given
        every { sessionManger.getUser() } returns UserMock.validUser

        // When/Then
        assertThrows<EiffelFlowException.AuthorizationException> {
            userRepository.createUser(user = UserMock.validUser)
        }
    }

    @Test
    fun `createUser should throw Exception when write to mongodb fails`() = runTest {
        //Given

        coEvery {
            usersCollection.insertOne(document = any(), options = any())
        } throws MongoException("Can't create this user")

        assertThrows<EiffelFlowException.IOException> {
            userRepository.createUser(UserMock.validUser)
        }
    }

    @Test
    fun `createUser should throw Exception when mapping failed`() = runTest {
        //Given
        every {
            userMapper.toDto(UserMock.validUser)
        } throws Throwable("Can't map this user")

        assertThrows<EiffelFlowException.IOException> {
            userRepository.createUser(UserMock.validUser)
        }
    }
    //endregion

    //region updateUser
    @Test
    fun `updateUser should update correct if the item exists`() = runTest {
        //Given
        coEvery {
            usersCollection.findOneAndUpdate(
                any<Bson>(),
                any<Bson>(),
                any()
            )
        } returns UserMock.VALID_USER_DTO

        // When
        val result = userRepository.updateUser(UserMock.adminUser)

        assertThat(result).isEqualTo(UserMock.adminUser)
    }

    @Test
    fun `updateUser should throw Exception when user is not found`() {
        runTest {
            // Given
            coEvery {
                usersCollection.findOneAndUpdate(
                    any<Bson>(),
                    any<Bson>(),
                    any()
                )
            } returns null

            // When & Then
            assertThrows<EiffelFlowException.NotFoundException> {
                userRepository.updateUser(user = UserMock.validUser)
            }
        }
    }

    @Test
    fun `updateUser should return Exception when writing to DB fails`() {
        runTest {
            // Given
            coEvery {
                usersCollection.findOneAndUpdate(
                    any<Bson>(),
                    any<List<Bson>>(),
                    any()
                )
            } throws MongoException("Can't update this user")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                userRepository.updateUser(user = UserMock.validUser)
            }
        }
    }

    //endregion

    //region deleteUser
    @Test
    fun `deleteUser should return the deleted user on success`() = runTest {
        coEvery {
            usersCollection.findOneAndDelete(
                any<Bson>(),
                any()
            )
        } returns UserMock.ADMIN_USER_DTO

        every {
            userMapper.fromDto(UserMock.ADMIN_USER_DTO)
        } returns UserMock.adminUser

        // When
        val result = userRepository.deleteUser(UserMock.adminUser.userId)

        // Then
        assertThat(result).isEqualTo(UserMock.adminUser)
    }

    @Test
    fun `deleteUser should throw Exception when user is not admin`() {
        runTest {
            // Given
            every { sessionManger.getUser() } returns UserMock.validUser

            // When & Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                userRepository.deleteUser(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `deleteUser should throw Exception when user is not found`() {
        runTest {
            //Given
            coEvery {
                usersCollection.findOneAndDelete(
                    any<Bson>(),
                    any()
                )
            } returns null

            // When & Then
            assertThrows<EiffelFlowException.NotFoundException> {
                userRepository.deleteUser(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `deleteUser should throw Exception when writing to DB fails`() {
        runTest {
            // Given
            coEvery {
                usersCollection.findOneAndDelete(
                    any<Bson>(),
                    any()
                )
            } throws MongoException("Can't delete this user")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                userRepository.deleteUser(UserMock.userToDelete.userId)
            }
        }
    }

    //endregion


    //region getUserById
    @Test
    fun `getUserById should return the user on success`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<MongoUserDto>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<MongoUserDto>>(0)
                collector.emit(UserMock.VALID_USER_DTO)
            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

            every {
                userMapper.fromDto(UserMock.VALID_USER_DTO)
            } returns UserMock.validUser

            //When
            val result = userRepository.getUserById(UserMock.validUser.userId)

            //Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }
    }

    @Test
    fun `getUserById should throw Exception when user is not found`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<MongoUserDto>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {

            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

            // When & Then
            assertThrows<EiffelFlowException.NotFoundException> {
                userRepository.getUserById(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `getUserById should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                usersCollection.find()
            } throws MongoException("Can't get User")

            // When & Then
            assertThrows<EiffelFlowException.NotFoundException> {
                userRepository.getUserById(UserMock.validUser.userId)
            }
        }
    }

    @Test
    fun `getUserById should throw Exception when when user is not admin `() {
        runTest {
            // Given
            every { sessionManger.getUser() } returns UserMock.validUser

            // When & Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                userRepository.getUserById(UUID.randomUUID())
            }
        }
    }
    //endregion


    // region getUsers
    @Test
    fun `getUsers should return list of users`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<MongoUserDto>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<MongoUserDto>>(0)
                UserMock.MULTIPLE_USER_DTO.forEach { collector.emit(it) }
            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

            every {
                userMapper.fromDto(any())
            } returnsMany UserMock.multipleUsers

            //When
            val result = userRepository.getUsers()

            //Then
            assertThat(result).containsExactlyElementsIn(UserMock.multipleUsers)
        }
    }

    @Test
    fun `getUsers should return empty list of users when DB is empty`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<MongoUserDto>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

            every {
                userMapper.fromDto(any())
            } returnsMany emptyList()

            //When
            val result = userRepository.getUsers()

            //Then
            assertThat(result).containsExactlyElementsIn(emptyList<User>())
        }
    }

    @Test
    fun `getUsers should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                usersCollection.find()
            } throws MongoException("Can't get User")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                userRepository.getUsers()
            }
        }
    }

    @Test
    fun `getUsers should throw Exception when when user is not admin `() {
        runTest {
            // Given
            every { sessionManger.getUser() } returns UserMock.validUser

            // When & Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                userRepository.getUsers()
            }
        }
    }
    //endregion

}