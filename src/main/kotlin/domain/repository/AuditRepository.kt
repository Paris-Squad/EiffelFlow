package org.example.domain.repository

import org.example.domain.model.AuditLog
import java.util.UUID

interface AuditRepository {

    fun getItemAuditLogById(itemId: UUID): Result<List<AuditLog>>

    fun getAuditLogs(): Result<List<AuditLog>>
}