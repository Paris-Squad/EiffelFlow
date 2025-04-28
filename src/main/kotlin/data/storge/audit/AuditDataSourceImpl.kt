package org.example.data.storge.audit

import org.example.data.storge.CsvStorageManager
import org.example.data.storge.Mapper
import org.example.domain.model.entities.AuditLog
import java.util.UUID

class AuditDataSourceImpl(
    private val auditMapper: Mapper<List<String>, AuditLog>,
    private val csvManager: CsvStorageManager
) : AuditDataSource {
    override fun createAuditLog(auditLog: AuditLog): Result<AuditLog> {
       val auditLogString = auditMapper.mapTo(auditLog)
        csvManager.writeLinesToFile(auditLogString.joinToString { "," })
        return Result.success(auditLog)
    }

    override fun updateAuditLog(auditLog: AuditLog): Result<AuditLog> {
        TODO("Not yet implemented")
    }

    override fun getAuditLogById(auditLogID: UUID): Result<AuditLog> {
        TODO("Not yet implemented")
    }

    override fun getAuditLogs(): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}