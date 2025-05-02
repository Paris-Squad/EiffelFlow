package org.example.domain.usecase.task

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.*

class ViewTaskHistoryUseCase (private val auditRepository: AuditRepository) {

    fun viewTaskHistory(taskId: UUID): Result<List<AuditLog>> = auditRepository.getTaskAuditLogById(taskId)
}