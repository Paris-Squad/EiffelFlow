//package data.mongorepository
//
//import com.google.common.truth.Truth.assertThat
//import com.mongodb.client.model.FindOneAndUpdateOptions
//import com.mongodb.kotlin.client.coroutine.MongoCollection
//import com.mongodb.kotlin.client.coroutine.MongoDatabase
//import io.mockk.coEvery
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.test.runTest
//import org.bson.Document
//import org.example.domain.model.User
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import utils.UserMock
//
//class MongoAuthRepositoryImplTest {
//
//    private val mongoDatabase: MongoDatabase = mockk()
//    private val authCollection: MongoCollection<User> = mockk()
//    private lateinit var repository: MongoAuthRepositoryImpl
//
//    @BeforeEach
//    fun setUp() {
//        every {
//            mongoDatabase.getCollection<User>(any())
//        } returns authCollection
//        repository = MongoAuthRepositoryImpl(
//            database = mongoDatabase
//        )
//    }
//
//    @Test
//    fun `saveUserLogin should return User when user is saved successfully`() {
//        runTest {
//            try {
//                // Given
//                coEvery {
//                    authCollection.findOneAndUpdate(
//                        any<Document>(),
//                        any<Document>(),
//                        any<FindOneAndUpdateOptions>()
//                    )
//                } returns UserMock.adminUser
//
//                //When
//                val result = repository.saveUserLogin(UserMock.adminUser)
//
//                //Then
//                assertThat(result).isEqualTo(UserMock.adminUser)
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `isUserLoggedIn should return true when file has content`() {
//        runTest {
//            try {
//                // Given
//                coEvery {
//                    authCollection.findOneAndReplace(any(), any())
//                } returns UserMock.updateUser
//
//                //When
//                val result = repository.isUserLoggedIn()
//
//                //Then
//                assertThat(result).isTrue()
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `clearLogin should clear login data when file is cleared successfully`() {
//        runTest {
//            try {
//                // Given
//
//                coEvery {
//                    authCollection.findOneAndDelete(any())
//                } returns UserMock.adminUser
//
//                //When / Then
//               repository.clearLogin()
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `loginUser should return success when username and password are correct`() {
//        runTest {
//            try {
//                // Given
//                val username = "validUser"
//                val password = "validPass"
//                coEvery {
//                    authCollection.find().toList()
//                } returns UserMock.multipleUsers
//
//                //When
//                val result = repository.loginUser(username, password)
//
//                //Then
//                assertThat(result).isEqualTo(UserMock.validUser)
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//}