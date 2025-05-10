package org.example.domain.usecase.task

import org.example.data.storage.SessionManger
import org.example.domain.model.Task
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository

class EditTaskUseCase(
    private val taskRepository: TaskRepository ,
    private val auditRepository: AuditRepository
) {
    suspend fun editTask(request: Task): Task {
        val originalTask = taskRepository.getTaskById(request.taskId)

        if (originalTask == request) throw EiffelFlowException.IOException("No changes detected")

        val changedField = detectChangedField(originalTask, request)

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

    private fun detectChangedField(original: Task, updated: Task): String {
        val changes = mutableListOf<String>()

        if (original.title != updated.title) changes.add("TITLE")
        if (original.description != updated.description) changes.add("DESCRIPTION")
        if (original.assignedId != updated.assignedId) changes.add("ASSIGNEE")
        if (original.state != updated.state) changes.add("STATE")
        if (original.role != updated.role) changes.add("ROLE")
        if (original.projectId != updated.projectId) changes.add("PROJECT")

        return changes.joinToString(", ") { it }
    }
}
