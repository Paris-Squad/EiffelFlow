package org.example.domain.usecase.task

import org.example.data.utils.SessionManger
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.*

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository ,
    private val auditRepository: AuditRepository
) {

    suspend fun deleteTask(taskId: UUID): Task {

        val deletedTask = taskRepository.deleteTask(taskId)
        val auditLog = deletedTask.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.DELETE,
                changedField = null,
                oldValue = deletedTask.toString(),
                newValue = ""
            )
         auditRepository.createAuditLog(auditLog)
        return deletedTask
    }
}