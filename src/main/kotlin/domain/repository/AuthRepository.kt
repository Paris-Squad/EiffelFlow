package org.example.domain.repository

import org.example.domain.model.User

interface AuthRepository {
    fun saveUserLogin(user: User): User
    fun isUserLoggedIn(): Boolean
    fun clearLogin()
    fun loginUser(username: String, password: String): User
}