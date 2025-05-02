package org.example.data.storage.user

import org.example.data.storage.FileDataSource
import org.example.data.storage.Mapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import java.io.FileNotFoundException
import java.util.UUID

class UserDataSourceImpl(
    private val userMapper: Mapper<String, User>,
    private val csvManager: FileDataSource
) : UserDataSource {
    override fun createUser(user: User): Result<User> {
        return try {
            val userAsCsv = userMapper.mapTo(user)
            csvManager.writeLinesToFile(userAsCsv)
            Result.success(user)
        } catch (e: Throwable) {
            Result.failure(EiffelFlowException.IOException(e.message))
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
        } catch (e: Throwable) {
            Result.failure(EiffelFlowException.IOException(e.message))
        }
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }
}