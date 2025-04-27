package org.example.domain.repository

import org.example.domain.model.AuditLog
import org.example.domain.model.Project
import java.util.UUID

interface AuditRepository {
    fun logChange(auditLog: AuditLog)
    fun getLogByItemId(itemId: UUID): AuditLog
    fun getAllLogs(): List<AuditLog>
}