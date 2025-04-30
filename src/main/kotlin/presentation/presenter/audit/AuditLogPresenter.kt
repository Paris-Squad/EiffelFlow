package org.example.presentation.presenter.audit

import org.example.domain.model.entities.AuditLog
import org.example.domain.usecase.audit.AuditUseCase
import java.util.UUID

class AuditLogPresenter(
    private val auditUseCase: AuditUseCase
) {
    fun getAllAuditLogs(): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    fun getAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }
}