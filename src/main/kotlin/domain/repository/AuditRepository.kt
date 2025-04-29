package org.example.domain.repository

import org.example.domain.model.entities.AuditLog
import java.util.UUID

interface AuditRepository {

    fun getAuditLogById(auditLogID: UUID): Result<List<AuditLog>>

    fun getAuditLogs(): Result<List<AuditLog>>
}