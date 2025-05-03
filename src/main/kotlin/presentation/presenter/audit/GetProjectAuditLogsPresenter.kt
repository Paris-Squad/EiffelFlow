package org.example.presentation.presenter.audit

import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import java.util.UUID

class GetProjectAuditLogsPresenter(
    private val getProjectAuditUseCase: GetProjectAuditUseCase
) {
    companion object {
        private const val ERROR_MESSAGE_NO_LOGS_FOUND = "No audit records were found for this project"
    }

    fun getProjectAuditLogsById(auditId: UUID): Result<List<AuditLog>> {
        return getProjectAuditUseCase
            .getProjectAuditLogsById(auditId).fold(
                onFailure = { error -> Result.failure(error) },

                onSuccess = { logs -> handleAuditLogsSuccess(logs) }
            )
    }

    private fun handleAuditLogsSuccess(logs: List<AuditLog>): Result<List<AuditLog>> {
        return if (logs.isEmpty())
            Result.failure(EiffelFlowException.NotFoundException(ERROR_MESSAGE_NO_LOGS_FOUND))
        else
            Result.success(logs)
    }
}

