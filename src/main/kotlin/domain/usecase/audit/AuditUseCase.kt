package org.example.domain.usecase.audit

import org.example.domain.model.entities.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class AuditUseCase(
    private val repository: AuditRepository
) {
    fun getAllAuditLogs(): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    fun getAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }
}