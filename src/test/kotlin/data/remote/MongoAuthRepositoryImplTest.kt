package data.remote

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.MongoAuthRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.runTest
import org.bson.Document
import org.bson.conversions.Bson
import org.example.data.MongoCollections
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock

class MongoAuthRepositoryImplTest {

    private lateinit var authCollection: MongoCollection<User>
    private lateinit var usersCollection: MongoCollection<User>
    private lateinit var authRepository: AuthRepository

    @BeforeEach
    fun setup() {
        authCollection = mockk(relaxed = true)
        usersCollection = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every {
            mockDatabase.getCollection<User>(MongoCollections.AUTH)
        } returns authCollection

        every {
            mockDatabase.getCollection<User>(MongoCollections.USERS)
        } returns usersCollection

        authRepository = MongoAuthRepositoryImpl(mockDatabase)
    }


    //region saveUserLogin
    @Test
    fun `saveUserLogin should return User when user is saved successfully`() =
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { authCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                authCollection.find()
            } returns mockFindFlow

            // When / Then
            val result = authRepository.saveUserLogin(UserMock.validUser)

            // Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }

    @Test
    fun `saveUserLogin should throw exception when DB operation fails`() =
        runTest {
            // Given
            coEvery {
                authCollection.find(any<Bson>())
            } throws MongoException("Failed to save user")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.saveUserLogin(UserMock.validUser)
            }
        }

    @Test
    fun `saveUserLogin should throw Exception if the user already saved`() =
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery {
                authCollection.find(any<Bson>())
            } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<User>>(0)
                collector.emit(UserMock.validUser)
            }

            coEvery {
                authCollection.find(any<Bson>())
            } returns mockFindFlow

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.saveUserLogin(UserMock.validUser)
            }
        }
    //endregion

    //region isUserLoggedIn
    @Test
    fun `isUserLoggedIn should return true when auth collection has content`() =
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { authCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<User>>(0)
                collector.emit(UserMock.validUser)
            }

            coEvery {
                authCollection.find()
            } returns mockFindFlow

            // When /Then
            val result = authRepository.isUserLoggedIn()

            // Then
            assertThat(result).isTrue()
        }

    @Test
    fun `isUserLoggedIn should throw exception when auth collection is empty`() =
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { authCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                authCollection.find()
            } returns mockFindFlow

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.isUserLoggedIn()
            }
        }

    @Test
    fun `isUserLoggedIn should throw Exception when failed to get user`() =
        runTest {
            // Given
            coEvery {
                authCollection.find()
            } throws MongoException("Can't get User")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.isUserLoggedIn()
            }
        }
    //endregion

    //region clearLogin
    @Test
    fun `clearLogin should clear auth collection and logout user`() =
        runTest {
            // Given
            SessionManger.login(UserMock.adminUser)

            coEvery {
                authCollection.deleteMany(any())
            } returns mockk()

            //when
            authRepository.clearLogin()

            //Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                SessionManger.getUser()
            }
        }

    @Test
    fun `clearLogin should throw exception when failed to remove the user`() {
        runTest {
            // Given
            coEvery {
                authCollection.deleteMany(Document(), any())
            } throws MongoException("Failed to remove user")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.clearLogin()
            }
        }
    }
    //endregion

    //region loginUser
    @Test
    fun `loginUser should return success when username and password are correct`() =
        runTest {
            // Given
            val username = UserMock.validUser.username
            val password = UserMock.validUser.password
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<User>>(0)
                collector.emit(UserMock.validUser)
            }
            coEvery {
                usersCollection.find()
            } returns mockFindFlow

            coEvery {
                authRepository.saveUserLogin(any())
            } returns UserMock.validUser

            // When
            val result = authRepository.loginUser(username, password)

            // Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }

    @Test
    fun `loginUser should throw exception when username and password are not correct`() =
        runTest {
            // Given
            val username = UserMock.adminUser.username
            val password = UserMock.adminUser.password
            val mockFindFlow = mockk<FindFlow<User>>()

            coEvery { usersCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }
            coEvery {
                authCollection.find()
            } returns mockFindFlow

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.loginUser(username, password)
            }
        }

    @Test
    fun `loginUser should throw exception when DB operation fails`() =
        runTest {
            // Given
            val username = UserMock.adminUser.username
            val password = UserMock.adminUser.password
            coEvery {
                usersCollection.find()
            } throws MongoException("Failed to get user")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.loginUser(username, password)
            }
        }
    //endregion
}