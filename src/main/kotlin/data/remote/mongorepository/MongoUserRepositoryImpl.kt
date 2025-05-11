package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.BaseRepository
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoUserDto
import org.example.data.remote.mapper.UserMapper
import org.example.data.utils.SessionManger
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
    private val auditRepository: AuditRepository,
    private val userMapper: UserMapper
) : BaseRepository(), UserRepository {

    private val usersCollection = database.getCollection<MongoUserDto>(collectionName = MongoCollections.USERS)

    override suspend fun createUser(user: User): User {
        validateAdminPermission()
        return wrapInTryCatch {
            val userDto = userMapper.toDto(user)
            usersCollection.insertOne(userDto)
            logAction(user, AuditLogAction.CREATE)
            user
        }
    }

    override suspend fun updateUser(user: User): User {
        return wrapInTryCatch {
            val userDto = userMapper.toDto(user)
            val updates = Updates.combine(
                Updates.set(MongoUserDto::username.name, userDto.username),
                Updates.set(MongoUserDto::password.name, userDto.password),
                Updates.set(MongoUserDto::role.name, userDto.role),
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq(MongoUserDto::_id.name, userDto._id)
            val oldUserDto = usersCollection.findOneAndUpdate(query, updates, options)

            if (oldUserDto == null) {
                throw EiffelFlowException.NotFoundException("User with _id ${user.userId} not found")
            }

            val oldUser = userMapper.fromDto(oldUserDto)
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
        }
    }

    override suspend fun deleteUser(userId: UUID): User {
        validateAdminPermission()
        return wrapInTryCatch {
            val query = eq(MongoUserDto::_id.name, userId.toString())
            val deletedUserDto = usersCollection.findOneAndDelete(query)

            if (deletedUserDto == null) {
                throw EiffelFlowException.NotFoundException("User with id $userId not found")
            }
            val deletedUser = userMapper.fromDto(deletedUserDto)
            logAction(
                user = deletedUser,
                actionType = AuditLogAction.DELETE
            )
            deletedUser
        }
    }

    override suspend fun getUserById(userId: UUID): User {
        validateAdminPermission()
        return wrapInTryCatch {
            val query = eq(MongoUserDto::_id.name, userId.toString())
            val userDto = usersCollection.find(query).firstOrNull()
            userDto ?: throw EiffelFlowException.NotFoundException("User with id $userId not found")
            val user = userMapper.fromDto(userDto)
            user
        }
    }

    override suspend fun getUsers(): List<User> {
        validateAdminPermission()
        return wrapInTryCatch {
            val usersDto = usersCollection.find().toList()
            usersDto.map { userMapper.fromDto(it) }
        }
    }

    private fun validateAdminPermission() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Only admin can create user")
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