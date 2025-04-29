package org.example.data.repository

import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.user.UserDataSource
import org.example.domain.model.entities.User
import org.example.domain.repository.UserRepository
import java.util.UUID

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val auditDataSource: AuditDataSource,
) : UserRepository {
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

}