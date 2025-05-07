package org.example.domain.usecase.audit

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class GetProjectAuditUseCase(
    private val auditRepository: AuditRepository
) {
    suspend fun getProjectAuditLogsById(projectId: UUID): List<AuditLog> =
        auditRepository.getProjectAuditLogById(projectId)
}