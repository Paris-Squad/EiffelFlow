package org.example.data.repository

import org.example.data.storage.audit.AuditDataSource
import org.example.domain.model.entities.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class AuditRepositoryImpl(
    private val auditDataSource: AuditDataSource
): AuditRepository {

    override fun getItemAuditLogById(itemId: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    override fun getAuditLogs(): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }
}