package data.mongorepository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class MongoUserRepositoryImpl: UserRepository {
    override suspend fun createUser(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(userId: UUID): User {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: UUID): User {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): List<User> {
        TODO("Not yet implemented")
    }

}