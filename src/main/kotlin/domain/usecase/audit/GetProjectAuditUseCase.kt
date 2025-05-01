package org.example.domain.usecase.audit

import org.example.domain.model.entities.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class GetProjectAuditUseCase(
    private val repository: AuditRepository
) {

    fun getProjectAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }
}