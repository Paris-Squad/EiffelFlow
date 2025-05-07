package org.example.domain.repository

import org.example.domain.model.AuditLog
import java.util.*

interface AuditRepository {

    suspend fun createAuditLog(auditLog: AuditLog): AuditLog

    suspend fun getTaskAuditLogById(taskId: UUID): List<AuditLog>

    suspend fun getProjectAuditLogById(projectId: UUID): List<AuditLog>

    suspend fun getAuditLogs(): List<AuditLog>
}