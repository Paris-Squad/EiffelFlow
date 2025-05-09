package org.example.domain.repository

import org.example.domain.model.User
import java.util.*

interface UserRepository {

    suspend fun createUser(user: User): User

    suspend fun updateUser(user: User): User

    suspend fun deleteUser(userId: UUID): User

    suspend fun getUserById(userId: UUID): User

    suspend fun getUsers(): List<User>
}