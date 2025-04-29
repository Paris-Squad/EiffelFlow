package org.example.data.storage.mapper

import org.example.data.storage.Mapper
import org.example.domain.model.entities.User
import org.example.data.utils.UserCsvColumnIndex
import org.example.domain.model.entities.RoleType
import java.util.UUID

class UserCsvMapper : Mapper<String, User> {

    override fun mapFrom(input: String): User {
        val parts = input.split(",")

        return User(
            userId = UUID.fromString(parts[UserCsvColumnIndex.USER_ID]),
            username = parts[UserCsvColumnIndex.USERNAME],
            password = parts[UserCsvColumnIndex.PASSWORD],
            role = RoleType.valueOf(parts[UserCsvColumnIndex.ROLE])
        )
    }

    override fun mapTo(output: User): String {
        return listOf(
            output.userId.toString(),
            output.username,
            output.password,
            output.role.name
        ).joinToString(",")
    }
}
