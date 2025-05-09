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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.example.data.remote.MongoCollections
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock
import java.util.UUID

class MongoUserRepositoryImplTest {

    private val sessionManger: SessionManger = mockk(relaxed = true)
    private lateinit var usersCollection: MongoCollection<User>
    private lateinit var auditRepository: AuditRepository
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        usersCollection = mockk(relaxed = true)
        auditRepository = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every { mockDatabase.getCollection<User>(MongoCollections.USERS) } returns usersCollection
        every { sessionManger.getUser() } returns UserMock.adminUser
        userRepository = MongoUserRepositoryImpl(mockDatabase, auditRepository)
    }

    //region createUser
    @Test
    fun `createUser should insert user if not exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<User>>()

        coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            usersCollection.insertOne(eq(UserMock.validUser), any())
        } returns mockk()

        //When
        val result = userRepository.createUser(UserMock.validUser)

        //Then
        assertThat(result).isEqualTo(UserMock.validUser)
    }

    @Test
    fun `createUser should throw if user already exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<User>>()
        coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow
        coEvery { mockFindFlow.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<User>>(0)
            collector.emit(UserMock.validUser)
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        //When /Then
        assertThrows(EiffelFlowException.IOException::class.java) {
            runBlocking { userRepository.createUser(UserMock.validUser) }
        }
    }

    @Test
    fun `createUser should throw Exception when audit log creation fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<User>>()

        coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            usersCollection.insertOne(eq(UserMock.validUser), any())
        } throws EiffelFlowException.IOException("Custom exception")

        //When then
        assertThrows<EiffelFlowException.IOException> {
            userRepository.createUser(user = UserMock.validUser)
        }

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
        val mockFindFlow = mockk<FindFlow<User>>()

        coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        coEvery {
            usersCollection.insertOne(eq(UserMock.validUser), any())
        } returns mockk()

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
        } returns UserMock.validUser

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
            assertThrows<EiffelFlowException.IOException> {
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

    @Test
    fun `updateUser should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            coEvery {
                auditRepository.createAuditLog(any())
            } throws Throwable("Can't Update User")

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
        } returns UserMock.adminUser
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

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
            assertThrows<EiffelFlowException.IOException> {
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

    @Test
    fun `deleteUser should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            coEvery {
                auditRepository.createAuditLog(any())
            } throws Throwable("Can't Delete User")

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
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<User>>(0)
                collector.emit(UserMock.validUser)
            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

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
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {

            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
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
            assertThrows<EiffelFlowException.IOException> {
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
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<User>>(0)
                UserMock.multipleUsers.forEach { collector.emit(it) }
            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

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
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                usersCollection.find()
            } returns mockFindFlow

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