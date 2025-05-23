package org.example.domain.usecase.audit

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository

class GetAllAuditLogsUseCase(
    private val auditRepository: AuditRepository
) {
    suspend fun getAllAuditLogs(): List<AuditLog> =
         auditRepository.getAuditLogs()
}