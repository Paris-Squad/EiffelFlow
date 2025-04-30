package org.example.data.storage.user

import org.example.data.storge.CsvStorageManager
import org.example.data.storge.Mapper
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.model.entities.User
import java.io.FileNotFoundException
import java.util.UUID

class UserDataSourceImpl(
    private val userMapper: Mapper<String, User>,
    private val csvManager: CsvStorageManager
) : UserDataSource {
    override fun createUser(user: User): Result<User> {
        return try {
            val userAsCsv = userMapper.mapTo(user)
            val line = userAsCsv + "\n"
            csvManager.writeLinesToFile(line)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(EiffelFlowException.UserCreationException(e.message))
        }
    }

    override fun updateUser(user: User): Result<User> {
        TODO("Not yet implemented")
    }

    override fun deleteUser(userId: UUID): Result<User> {
        TODO("Not yet implemented")
    }

    override fun getUserById(userId: UUID): Result<User> {
        TODO("Not yet implemented")
    }

    override fun getUsers(): Result<List<User>> {
        return try {
            val lines = csvManager.readLinesFromFile()
            val users = lines
                .filter { it.isNotBlank() }
                .map { line -> userMapper.mapFrom(line) }

            Result.success(users)
        } catch (_: FileNotFoundException) {
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(EiffelFlowException.UserStorageException(e.message))
        }
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }
}