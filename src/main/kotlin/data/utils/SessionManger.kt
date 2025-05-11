package org.example.data.utils

import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User

object SessionManger {

    private var user: User? = null

    @Throws(EiffelFlowException.AuthorizationException::class)
    fun getUser(): User {
        return user ?: throw EiffelFlowException.AuthorizationException("User is not logged in")
    }

    fun isAdmin(): Boolean = getUser().role == RoleType.ADMIN

    fun isLoggedIn(): Boolean = user != null

    fun logout() {
        user = null
    }

    fun login(user: User) {
        this.user = user
    }
}