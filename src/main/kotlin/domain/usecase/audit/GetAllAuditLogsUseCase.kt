package org.example.domain.usecase.audit

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository

class GetAllAuditLogsUseCase(
    private val auditRepository: AuditRepository
) {
    fun getAllAuditLogs(): Result<List<AuditLog>> =
         auditRepository.getAuditLogs()
}