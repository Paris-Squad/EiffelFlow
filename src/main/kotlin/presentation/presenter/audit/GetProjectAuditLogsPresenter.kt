package org.example.presentation.presenter.audit

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import java.util.UUID

class GetProjectAuditLogsPresenter(
    private val getProjectAuditUseCase: GetProjectAuditUseCase
) {

     fun getProjectAuditLogsById(auditId: UUID): List<AuditLog> {
         return try {
             runBlocking {
                 getProjectAuditUseCase.getProjectAuditLogsById(auditId)
             }
         } catch (e: EiffelFlowException) {
             throw e
         } catch (e: Exception) {
             throw RuntimeException("An error occurred while get the project AuditLogs: ${e.message}", e)
         }
    }

}

