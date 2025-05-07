package org.example.domain.usecase.audit

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class GetTaskAuditUseCase(
    private val auditRepository: AuditRepository
) {
    suspend fun getTaskAuditLogsById(taskId: UUID): List<AuditLog> =
        auditRepository.getTaskAuditLogById(taskId)
}