package org.example.data.migration

import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.UserRepository

class UserCsvToMongoMigrator(
    private val csvRepository: UserRepository,
    private val mongoRepository: UserRepository
) {
    suspend fun migrateUsers(): Boolean {
        try {
            val csvUsers = csvRepository.getUsers()

            csvUsers.forEach {
                mongoRepository.createUser(it)
            }
            return true
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("migrateUsers from CSV to Mongo Failed because ${exception.message}")
        }
    }
}
