package org.example.data.storge.mapper

import org.example.data.storge.CsvMapper
import org.example.domain.model.User

class UserCsvMapper : CsvMapper<User> {
    override fun fromCsv(columns: List<String>): User {
        TODO()
    }

    override fun toCsv(entity: User): List<String> {
        TODO()
    }
}
