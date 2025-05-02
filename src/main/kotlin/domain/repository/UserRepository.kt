package org.example.domain.repository

import org.example.domain.model.User
import java.util.UUID

interface UserRepository {
    fun createUser(user: User, createdBy: User): Result<User>
    fun updateUser(user: User): Result<User>
    fun deleteUser(userId: UUID): Result<User>
    fun getUserById(userId: UUID): Result<User>
    fun getUsers(): Result<List<User>>
}