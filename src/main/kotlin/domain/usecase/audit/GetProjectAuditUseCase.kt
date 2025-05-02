package org.example.domain.usecase.audit

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.*

class GetProjectAuditUseCase(
    private val auditLogRepository: AuditRepository
) {
    fun getProjectAuditLogsById(auditId: UUID): Result<List<AuditLog>> =
        auditLogRepository.getProjectAuditLogById(auditId)
}