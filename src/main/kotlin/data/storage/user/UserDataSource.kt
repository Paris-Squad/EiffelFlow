package org.example.data.storage.user

import org.example.domain.model.User
import java.util.UUID

interface UserDataSource {
    fun createUser(user: User): Result<User>

    fun updateUser(user: User): Result<User>

    fun deleteUser(userId: UUID): Result<User>

    fun getUserById(userId: UUID): Result<User>

    fun getUsers(): Result<List<User>>
}