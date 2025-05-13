package org.example.domain.usecase.task

import org.example.data.utils.SessionManger
import org.example.domain.model.Task
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.utils.getFieldChanges

class EditTaskUseCase(
    private val taskRepository: TaskRepository ,
    private val auditRepository: AuditRepository
) {
    suspend fun editTask(request: Task): Task {
        val originalTask = taskRepository.getTaskById(request.taskId)

        val changedFields = originalTask.getFieldChanges(request)

        if (changedFields.isEmpty()) {
            throw EiffelFlowException.IOException("No changes detected")
        }

         val changedField = changedFields.joinToString(", ") { it.fieldName }

        val updatedTask =  taskRepository.updateTask(
            task = request,
            oldTask = originalTask,
            changedField = changedField
        )

        val auditLog = updatedTask.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.UPDATE,
                changedField = changedField,
                oldValue = originalTask.toString(),
                newValue = updatedTask.toString()
            )

         auditRepository.createAuditLog(auditLog)
        return updatedTask
    }

}
