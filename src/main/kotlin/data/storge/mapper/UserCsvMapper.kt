package org.example.data.storge.mapper


import org.example.data.storge.Mapper
import org.example.domain.model.entities.User

class UserCsvMapper : Mapper<List<String>, User> {
    override fun mapFrom(input: List<String>): User {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: User): List<String> {
        TODO("Not yet implemented")
    }

}
