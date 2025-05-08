package data.mongorepository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.example.data.MongoCollections
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import java.util.UUID

class MongoUserRepositoryImpl(
    private val database: MongoDatabase,
    private val auditRepository: AuditRepository
) : UserRepository {

    private val usersCollection = database.getCollection<User>(collectionName = MongoCollections.USERS)

    override suspend fun createUser(user: User): User {
        try {
            usersCollection.insertOne(user)
            return user
        } catch (e: MongoException) {
            throw EiffelFlowException.IOException("Can't create User because ${e.message}")
        }catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't create User because ${e.message}")
        }catch (e: Throwable) {
            throw EiffelFlowException.IOException("Can't create User because ${e.message}")
        }
    }

    override suspend fun updateUser(user: User): User {
        try {
            val query = eq("userId", user.userId)
            val updatedUser = usersCollection.findOneAndReplace(query, user)
            return updatedUser ?: throw EiffelFlowException.NotFoundException("User with id ${user.userId} not found")
        } catch (e: MongoException) {
            throw EiffelFlowException.IOException("Can't update User with id ${user.userId} because ${e.message}")
        }
    }

    override suspend fun deleteUser(userId: UUID): User {
        try {
            val query = eq("userId", userId)
            val deletedUser = usersCollection.findOneAndDelete(query)
            return deletedUser ?: throw EiffelFlowException.NotFoundException("User with id $userId not found")
        } catch (e: MongoException) {
            throw EiffelFlowException.IOException("Can't delete User with id $userId because ${e.message}")
        }
    }

    override suspend fun getUserById(userId: UUID): User {
        try {
            val user = usersCollection.find(eq("userId", userId)).firstOrNull()
            return user ?: throw EiffelFlowException.NotFoundException("User with id $userId not found")
        } catch (exception: MongoException) {
            throw EiffelFlowException.IOException("Can't get User with id $userId because ${exception.message}")
        }
    }

    override suspend fun getUsers(): List<User> {
        try {
            val users = mutableListOf<User>()
            usersCollection.find().collect { user ->
                users.add(user)
            }
            return users
        } catch (exception: MongoException) {
            throw EiffelFlowException.IOException("Can't get Users because ${exception.message}")
        }
    }
}