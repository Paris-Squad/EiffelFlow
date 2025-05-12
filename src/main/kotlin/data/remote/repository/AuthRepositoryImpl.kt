package data.remote.repository

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document
import org.example.data.BaseRepository
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoUserDto
import org.example.data.remote.mapper.UserMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class AuthRepositoryImpl(
    database: MongoDatabase,
    private val userMapper: UserMapper
) : BaseRepository(), AuthRepository {

    private val authCollection = database.getCollection<MongoUserDto>(collectionName = MongoCollections.AUTH)
    private val usersCollection = database.getCollection<MongoUserDto>(collectionName = MongoCollections.USERS)

    override suspend fun saveUserLogin(user: User): User {
        return executeSafely {
            val userDto = userMapper.toDto(user)
            authCollection.insertOne(userDto)
            user
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return executeSafely {
            val currentUserDto = authCollection.find().firstOrNull()
            currentUserDto ?: return@executeSafely false
            val currentUser = userMapper.fromDto(currentUserDto)
            SessionManger.login(currentUser)
            true
        }
    }

    override suspend fun clearLogin() {
        return executeSafely {
            authCollection.deleteMany(Document())
            SessionManger.logout()
        }
    }

    override suspend fun loginUser(username: String, password: String): User {
        return executeSafely {
            val userDto = findUserByCredentials(username, password)
            userDto ?: throw EiffelFlowException.NotFoundException("Invalid Credentials")

            val existUser = userMapper.fromDto(userDto)
            val savedUser = saveUserLogin(existUser)
            SessionManger.login(savedUser)
            savedUser
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