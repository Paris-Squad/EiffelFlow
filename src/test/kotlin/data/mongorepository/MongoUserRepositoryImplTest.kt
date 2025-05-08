package data.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.example.data.MongoCollections
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class MongoUserRepositoryImplTest {

    private val sessionManger: SessionManger = mockk(relaxed = true)
    private lateinit var usersCollection: MongoCollection<User>
    private lateinit var auditRepository: AuditRepository
    private lateinit var repository: MongoUserRepositoryImpl

    @BeforeEach
    fun setup() {
        usersCollection = mockk(relaxed = true)
        auditRepository = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every { mockDatabase.getCollection<User>(MongoCollections.USERS) } returns usersCollection
        every { sessionManger.getUser() } returns UserMock.adminUser
        repository = MongoUserRepositoryImpl(mockDatabase, auditRepository)
    }

    @Test
    fun `createUser should insert user if not exists`() = runTest {
        val mockFindFlow = mockk<FindFlow<User>>()

        coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }

        coEvery {
            usersCollection.insertOne(eq(UserMock.validUser), any())
        } returns mockk()

        val result = repository.createUser(UserMock.validUser)
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

        assertThrows(EiffelFlowException.IOException::class.java) {
            runBlocking { repository.createUser(UserMock.validUser) }
        }
    }

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
        val result = repository.updateUser(UserMock.validUser)

        assertThat(result).isEqualTo(UserMock.validUser)
    }

    @Test
    fun `deleteUser should return the deleted user on success`() = runTest {
        coEvery {
            usersCollection.findOneAndDelete(
                any<Bson>(),
                any()
            )
        } returns UserMock.adminUser

        // When
        val result = repository.deleteUser(UserMock.adminUser.userId)

        // Then
        assertThat(result).isEqualTo(UserMock.adminUser)
    }

    @Test
    fun `getTasks should return list of tasks`() {
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
            val result = repository.getUsers()

            //Then
            assertThat(result).containsExactlyElementsIn(UserMock.multipleUsers)
        }
    }

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
            val result = repository.getUserById(UserMock.validUser.userId)

            //Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }
    }

}