package org.example.data.storage.audit

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.Mapper
import org.example.domain.model.entities.AuditLog
import java.util.UUID

class AuditDataSourceImpl(
    private val auditMapper: Mapper<String, AuditLog>,
    private val csvManager: CsvStorageManager
) : AuditDataSource {
    override fun createAuditLog(auditLog: AuditLog): Result<AuditLog> {
        TODO("Not yet implemented")
    }

    override fun getAuditLogById(auditLogID: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    override fun getAuditLogs(): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}