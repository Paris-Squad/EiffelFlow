package data.mongorepository

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document
import org.example.data.MongoCollections
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class MongoAuthRepositoryImpl(
    private val database: MongoDatabase
) : AuthRepository {

    private val authCollection = database.getCollection<User>(collectionName = MongoCollections.AUTH)
    private val usersCollection = database.getCollection<User>(collectionName = MongoCollections.USERS)

    override suspend fun saveUserLogin(user: User): User {
        try {
            val existingUser = authCollection.find(eq("userId", user.userId)).firstOrNull()
            if (existingUser != null) {
                throw EiffelFlowException.IOException("User with userId ${user.userId} already exists")
            }
            authCollection.insertOne(user)
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
            val existUser = usersCollection.find(
                and(
                    eq("username", username),
                    eq("password", password)
                )
            ).firstOrNull()

            existUser ?: throw EiffelFlowException.NotFoundException("Invalid username or password")
            val savedUser = saveUserLogin(existUser)
            SessionManger.login(savedUser)
            return savedUser
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't login user because ${throwable.message}")
        }
    }
}