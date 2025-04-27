package org.example.domain.repository

import org.example.domain.model.User
import java.util.UUID

interface UserRepository {
    fun createUser(user: User): User
    fun updateUser(user: User): User
    fun deleteUser(userId: UUID): User
    fun getUser(userId: UUID): User
    fun getUsers(): List<User>
}