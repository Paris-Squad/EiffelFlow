package org.example.data.storge.mapper


import org.example.data.storge.Mapper
import org.example.domain.model.entities.User

class UserCsvMapper : Mapper<String, User> {
    override fun mapFrom(input: String): User {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: User): String {
        TODO("Not yet implemented")
    }

}
