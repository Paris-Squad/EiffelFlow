package data.mongorepository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.MongoCollections
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class MongoAuthRepositoryImpl(
    private val database: MongoDatabase
) : AuthRepository {

    private val authCollection = database.getCollection<User>(collectionName = MongoCollections.AUTH)

    override suspend fun saveUserLogin(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun isUserLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun clearLogin() {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(username: String, password: String): User {
        TODO("Not yet implemented")
    }
}