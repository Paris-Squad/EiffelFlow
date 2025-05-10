package data.mongorepository

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoUserDto
import org.example.data.remote.mapper.UserMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class MongoAuthRepositoryImpl(
    database: MongoDatabase,
    private val userMapper: UserMapper
) : AuthRepository {

    private val authCollection = database.getCollection<MongoUserDto>(collectionName = MongoCollections.AUTH)
    private val usersCollection = database.getCollection<MongoUserDto>(collectionName = MongoCollections.USERS)

    override suspend fun saveUserLogin(user: User): User {
        try {
            val userDto = userMapper.toDto(user)
            authCollection.insertOne(userDto)
            return user
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't save user login because ${exception.message}")
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        try {
            authCollection.find().firstOrNull() ?: throw EiffelFlowException.NotFoundException(
                "User is not logged in"
            )
            return true
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't check if user is logged in because ${exception.message}")
        }
    }

    override suspend fun clearLogin() {
        try {
            authCollection.deleteMany(Document())
            SessionManger.logout()
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't clear login because ${throwable.message}")
        }
    }

    override suspend fun loginUser(username: String, password: String): User {
        try {
            val userDto = findUserByCredentials(username, password)
            userDto ?: throw EiffelFlowException.NotFoundException("Invalid username or password")

            val existUser = userMapper.fromDto(userDto)
            val savedUser = saveUserLogin(existUser)
            SessionManger.login(savedUser)
            return savedUser
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't login user because ${throwable.message}")
        }
    }

    private suspend fun findUserByCredentials(username: String, password: String): MongoUserDto? {
        val userNameQuery = eq(MongoUserDto::username.name, username)
        val passwordQuery = eq(MongoUserDto::password.name, password)
        val userDto = usersCollection.find(
            and(userNameQuery, passwordQuery)
        ).firstOrNull()
        return userDto
    }
}