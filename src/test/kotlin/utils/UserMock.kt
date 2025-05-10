package utils

import org.example.data.remote.mapper.UserMapper
import org.example.domain.model.RoleType
import org.example.domain.model.User
import java.io.FileNotFoundException
import java.util.UUID

object UserMock {
    private val userMapper = UserMapper()
    
    val validUser = User(
        userId = UUID.fromString("11111111-1111-1111-1111-111111111111"),
        username = "validUser",
        password = "validPass",
        role = RoleType.MATE
    )
    
    val VALID_USER_DTO = userMapper.toDto(validUser)

    val adminUser = User(
        userId = UUID.fromString("33333333-3333-3333-3333-333333333333"),
        username = "testuser",
        password = "P@ssw0rd",
        role = RoleType.ADMIN
    )

    val ADMIN_USER_DTO = userMapper.toDto(adminUser)
    
    val updateUser = User(
        userId = UUID.fromString("44444444-4444-4444-4444-444444444444"),
        username = "test",
        password = "test",
        role = RoleType.MATE
    )

    val UPDATE_USER_DTO = userMapper.toDto(updateUser)
    
    val existingUser = User(
        userId = updateUser.userId,
        username = "old-test",
        password = "old-test",
        role = RoleType.MATE
    )

    val userToDelete = User(
        userId = UUID.fromString("55555555-5555-5555-5555-555555555555"),
        username = "test",
        password = "test",
        role = RoleType.MATE
    )

    val userById = User(
        userId = UUID.fromString("66666666-6666-6666-6666-666666666666"),
        username = "test",
        password = "test",
        role = RoleType.MATE
    )

    val multipleUsers = listOf(
        User(
            userId = UUID.fromString("77777777-7777-7777-7777-777777777777"),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        ),
        User(
            userId = UUID.fromString("88888888-8888-8888-8888-888888888888"),
            username = "test2",
            password = "test2",
            role = RoleType.MATE
        )
    )
    
    val MULTIPLE_USER_DTO = multipleUsers.map { userMapper.toDto(it) }

    const val USER_CSV = "user-csv-string"
    const val OLD_USER_CSV = "old-user-csv"
    const val NEW_USER_CSV = "new-user-csv"

    val fileNotFoundException = FileNotFoundException("File not found")
}
