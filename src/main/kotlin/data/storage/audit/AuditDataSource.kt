package org.example.data.storage.audit

import org.example.domain.model.entities.AuditLog
import java.util.UUID


interface AuditDataSource {

    fun createAuditLog(auditLog: AuditLog): Result<AuditLog>

    fun getAuditLogById(auditLogID: UUID): Result<AuditLog>

    fun getAuditLogs(): Result<List<AuditLog>>
}