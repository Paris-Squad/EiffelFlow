package org.example.data.migration

import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository

class AuditLogsCsvToMongoMigrator(
    private val csvRepository: AuditRepository,
    private val mongoRepository: AuditRepository
) {
    suspend fun migrateAuditLogs(): Boolean {
        try {
            val csvAuditLogs = csvRepository.getAuditLogs()

            csvAuditLogs.forEach {
                mongoRepository.createAuditLog(it)
            }
            return true
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("migrateAuditLogs from CSV to Mongo Failed because ${exception.message}")
        }
    }
}