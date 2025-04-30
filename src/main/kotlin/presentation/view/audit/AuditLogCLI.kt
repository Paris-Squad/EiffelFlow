package org.example.presentation.view.audit

import org.example.domain.model.entities.AuditLog
import org.example.presentation.presenter.audit.AuditLogPresenter
import java.util.UUID

class AuditLogCLI(
    private val auditLogPresenter: AuditLogPresenter
) {
    fun getAllAuditLogs(): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }

    fun getAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }
}