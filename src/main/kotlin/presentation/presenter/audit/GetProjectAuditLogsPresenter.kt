package org.example.presentation.presenter.audit

import org.example.domain.model.entities.AuditLog
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import java.util.UUID

class GetProjectAuditLogsPresenter(
    private val getProjectAuditUseCase: GetProjectAuditUseCase
) {
    fun getProjectAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        TODO("Not yet implemented")
    }
}