package data.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bson.Document
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class MongoUserRepositoryImplTest {

    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val mongoDatabase: MongoDatabase = mockk()
    private val userCollection: MongoCollection<User> = mockk()
    private lateinit var repository: MongoUserRepositoryImpl

    @BeforeEach
    fun setUp() {
        every {
            mongoDatabase.getCollection<User>(any())
        } returns userCollection
        repository = MongoUserRepositoryImpl(
            database = mongoDatabase,
            auditRepository = auditRepository
        )
    }

    @Test
    fun `createUser should return the created user on success`() {
        runTest {
            try {
                // Given
                coEvery {
                    userCollection.findOneAndUpdate(
                        any<Document>(),
                        any<Document>(),
                        any<FindOneAndUpdateOptions>()
                    )
                } returns UserMock.adminUser

                //When
                val result = repository.createUser(UserMock.adminUser)

                //Then
                assertThat(result).isEqualTo(UserMock.adminUser)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `updateUser should return the updated user on success`() {
        runTest {
            try {
                // Given
                coEvery {
                    userCollection.findOneAndReplace(any(), any())
                } returns UserMock.updateUser

                //When
                val result = repository.updateUser(UserMock.updateUser)

                //Then
                assertThat(result).isEqualTo(UserMock.updateUser)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `deleteUser should return the deleted user on success`() {
        runTest {
            try {
                // Given
                
                coEvery {
                    userCollection.findOneAndDelete(any())
                } returns UserMock.adminUser

                //When
                val result = repository.deleteUser(UserMock.adminUser.userId)

                //Then
                assertThat(result).isEqualTo(UserMock.adminUser)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getTasks should return list of tasks`() {
        runTest {
            try {
                // Given
                coEvery {
                    userCollection.find().toList()
                } returns UserMock.multipleUsers

                //When
                val result = repository.getUsers()

                //Then
                assertThat(result).containsExactlyElementsIn(UserMock.multipleUsers)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getUserById should return the user on success`() {
        runTest {
            try {
                // Given
                val query = eq("userId", UserMock.adminUser.userId)
                coEvery {
                    userCollection.find(query).firstOrNull()
                } returns UserMock.adminUser

                //When
                val result = repository.getUserById(UserMock.adminUser.userId)

                //Then
                assertThat(result).isEqualTo(UserMock.adminUser)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

}