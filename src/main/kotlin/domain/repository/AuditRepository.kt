package org.example.domain.repository

import org.example.domain.model.AuditLog
import java.util.*

interface AuditRepository {

    fun createAuditLog(auditLog: AuditLog): AuditLog

    fun getTaskAuditLogById(taskId: UUID): List<AuditLog>

    fun getProjectAuditLogById(projectId: UUID): List<AuditLog>

    fun getAuditLogs(): List<AuditLog>
}