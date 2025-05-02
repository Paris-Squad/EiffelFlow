package org.example.presentation.presenter.audit

import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.view.audit.GetProjectAuditLogsCLI
import java.util.UUID

class GetProjectAuditLogsPresenter(
    private val getProjectAuditUseCase: GetProjectAuditUseCase
) {
    fun getProjectAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        val projectAuditLogs = getProjectAuditUseCase.getProjectAuditLogsById(auditId)
        return projectAuditLogs.fold(
            onSuccess = { logs ->
                if (logs.isEmpty()) {
                    Result.failure(Exception("No audit logs found for this project."))
                } else {
                    Result.success(logs)
                }
            },
            onFailure = { error ->
                Result.failure(Exception("Failed to load logs: ${error.message}"))
            }
        )
    }
}
