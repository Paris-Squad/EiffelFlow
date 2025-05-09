package org.example.data.storage

import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User

object SessionManger {

    private var user: User?  = User(
        username = "admin",
        password = "Admin@123",
        role = RoleType.ADMIN
    )

    @Throws(EiffelFlowException.AuthorizationException::class)
    fun getUser(): User{
        return user ?: throw EiffelFlowException.AuthorizationException("User is not logged in")
    }

    fun isAdmin(): Boolean = user?.role == RoleType.ADMIN

    fun isLoggedIn(): Boolean = user != null

    fun logout() {
        user = null
    }

    fun login(user: User) {
        this.user = user
    }
}