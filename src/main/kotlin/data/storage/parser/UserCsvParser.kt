package org.example.data.storage.parser

import org.example.data.storage.CsvParser
import org.example.domain.model.User
import org.example.data.utils.UserCsvColumnIndex
import org.example.domain.model.RoleType
import java.util.UUID

class UserCsvParser : CsvParser<User> {

    override fun parseCsvLine(csvLine: String): User {
        val parts = csvLine.split(",")
        return User(
            userId = UUID.fromString(parts[UserCsvColumnIndex.USER_ID]),
            username = parts[UserCsvColumnIndex.USERNAME],
            password = parts[UserCsvColumnIndex.PASSWORD],
            role = RoleType.valueOf(parts[UserCsvColumnIndex.ROLE])
        )
    }

    override fun serialize(item: User): String {
        return listOf(
            item.userId.toString(),
            item.username,
            item.password,
            item.role.name
        ).joinToString(",")
    }
}
