package org.example.data.storge.mapper

import org.example.data.storge.Mapper
import org.example.domain.model.entities.User
import org.example.data.utils.UserCsvColumnIndex
import org.example.domain.model.entities.RoleType
import java.util.UUID

class UserCsvMapper : Mapper<String, User> {

    override fun mapFrom(input: String): User {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: User): String {
        TODO("Not yet implemented")
    }
}
