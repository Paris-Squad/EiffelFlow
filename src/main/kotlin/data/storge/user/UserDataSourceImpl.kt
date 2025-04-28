package org.example.data.storge.user

import org.example.data.storge.CsvStorageManager
import org.example.data.storge.Mapper
import org.example.domain.model.entities.User
import java.util.UUID

class UserDataSourceImpl(
    private val userMapper: Mapper<List<String>, User>,
    private val csvManager: CsvStorageManager
) : UserDataSource {
    override fun createUser(user: User): Result<User> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    companion object {
        const val FILE_NAME = "users.csv"
    }

}