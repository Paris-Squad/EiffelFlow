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

    fun getProjectAuditLogsById(auditId: UUID): List<AuditLog> {
        val projectAuditLogs = getProjectAuditUseCase.getProjectAuditLogsById(auditId)

        if (projectAuditLogs.isEmpty())
            throw EiffelFlowException.NotFoundException(ERROR_MESSAGE_NO_LOGS_FOUND)
        else
            return projectAuditLogs
    }
}

