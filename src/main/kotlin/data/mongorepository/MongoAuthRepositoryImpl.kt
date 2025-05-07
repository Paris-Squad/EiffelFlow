package data.mongorepository

import org.example.domain.model.User
import org.example.domain.repository.AuthRepository

class MongoAuthRepositoryImpl : AuthRepository {
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