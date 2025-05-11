package data.remote.repository

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
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class UserRepositoryImpl(
    database: MongoDatabase,
    private val userMapper: UserMapper
) : BaseRepository(), UserRepository {

    private val usersCollection = database.getCollection<MongoUserDto>(collectionName = MongoCollections.USERS)

    override suspend fun createUser(user: User): User {
        return wrapInTryCatch {
            val userDto = userMapper.toDto(user)
            usersCollection.insertOne(userDto)
            user
        }
    }

    override suspend fun updateUser(user: User): User {
        return wrapInTryCatch {
            val userDto = userMapper.toDto(user)
            val updates = Updates.combine(
                Updates.set(MongoUserDto::username.name, userDto.username),
                Updates.set(MongoUserDto::password.name, userDto.password),
                Updates.set(MongoUserDto::role.name, userDto.role)
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq(MongoUserDto::_id.name, userDto._id)
            val oldUserDto = usersCollection.findOneAndUpdate(query, updates, options)

            oldUserDto ?: throw EiffelFlowException.NotFoundException("User with id ${user.userId} not found")
            user
        }
    }

    override suspend fun deleteUser(userId: UUID): User {
        return wrapInTryCatch {
            val query = eq(MongoUserDto::_id.name, userId.toString())
            val deletedUserDto = usersCollection.findOneAndDelete(query)

            if (deletedUserDto == null) {
                throw EiffelFlowException.NotFoundException("User with id $userId not found")
            }
            val deletedUser = userMapper.fromDto(deletedUserDto)
            deletedUser
        }
    }

    override suspend fun getUserById(userId: UUID): User {
        return wrapInTryCatch {
            val query = eq(MongoUserDto::_id.name, userId.toString())
            val userDto = usersCollection.find(query).firstOrNull()
            userDto ?: throw EiffelFlowException.NotFoundException("User with id $userId not found")
            val user = userMapper.fromDto(userDto)
            user
        }
    }

    override suspend fun getUsers(): List<User> {
        return wrapInTryCatch {
            val usersDto = usersCollection.find().toList()
            usersDto.map { userMapper.fromDto(it) }
        }
    }


}