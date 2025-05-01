package utils


import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import java.util.UUID

object UserMock {
    val validUser = User(
        userId = UUID.fromString("11111111-1111-1111-1111-111111111111"),
        username = "validUser",
        password = "validPass",
        role = RoleType.MATE
    )

    val invalidUser = User(
        userId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
        username = "invalidUser",
        password = "wrongPass",
        role = RoleType.MATE
    )

    val adminUser = User(
        userId = UUID.fromString("33333333-3333-3333-3333-333333333333"),
        username = "testuser",
        password = "P@ssw0rd",
        role = RoleType.ADMIN
        )

    val userList = listOf(validUser, invalidUser)
}