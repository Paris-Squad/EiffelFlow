package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.example.data.MongoCollections
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.UserRepository
import org.example.domain.utils.getFieldChanges
import java.util.UUID

class MongoUserRepositoryImpl(
    database: MongoDatabase,
    private val auditRepository: AuditRepository
) : UserRepository {

    private val usersCollection = database.getCollection<User>(collectionName = MongoCollections.USERS)

    override suspend fun createUser(user: User): User {
        try {
            val existingUser = usersCollection.find(eq("userId", user.userId)).firstOrNull()
            if (existingUser != null) {
                throw EiffelFlowException.IOException("User with userId ${user.userId} already exists")
            }
            usersCollection.insertOne(user)
            logAction(user, AuditLogAction.CREATE)
            return user
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't create User because ${exception.message}")
        }
    }

    override suspend fun updateUser(user: User): User {
        try {
            val updates = Updates.combine(
                Updates.set(User::username.name, user.username),
                Updates.set(User::password.name, user.password),
                Updates.set(User::role.name, user.role),
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq("userId", user.userId)
            val oldUser = usersCollection.findOneAndUpdate(query, updates, options)

            return oldUser?.let {
                val fieldChanges = oldUser.getFieldChanges(user)
                val changedFieldsNames = fieldChanges.map { it.fieldName }
                val oldValues = fieldChanges.map { it.oldValue }
                val newValues = fieldChanges.map { it.newValue }
                logAction(
                    user = user,
                    actionType = AuditLogAction.UPDATE,
                    changedField = changedFieldsNames.toString(),
                    oldValue = oldValues.toString(),
                    newValue = newValues.toString(),
                )
                user
            } ?: throw EiffelFlowException.NotFoundException("User with id ${user.userId} not found")
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't update User with id ${user.userId} because ${exception.message}")
        }
    }

    override suspend fun deleteUser(userId: UUID): User {
        try {
            val query = eq("userId", userId)
            val deletedUser = usersCollection.findOneAndDelete(query)

            return deletedUser?.let {
                logAction(
                    user = deletedUser,
                    actionType = AuditLogAction.DELETE
                )
                deletedUser
            } ?: throw EiffelFlowException.NotFoundException("User with id $userId not found")

        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't delete User with id $userId because ${exception.message}")
        }
    }

    override suspend fun getUserById(userId: UUID): User {
        try {
            val user = usersCollection.find(eq("userId", userId)).firstOrNull()
            return user ?: throw EiffelFlowException.NotFoundException("User with id $userId not found")
        } catch (exception: Throwable) {
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
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Users because ${exception.message}")
        }
    }

    private suspend fun logAction(
        user: User,
        actionType: AuditLogAction,
        changedField: String? = null,
        oldValue: String? = null,
        newValue: String = user.toString()
    ) {
        val auditLog = user.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = actionType,
            changedField = changedField,
            oldValue = oldValue,
            newValue = newValue,
        )
        auditRepository.createAuditLog(auditLog)
    }
}