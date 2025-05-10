package org.example.domain.usecase.task

import org.example.data.storage.SessionManger
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository ,
    private val auditRepository: AuditRepository
) {
    suspend fun createTask(task: Task): Task {
        val createdTask = taskRepository.createTask(task)
        val auditLog = task.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.CREATE,
                changedField = null,
                oldValue = null,
                newValue = task.title
            )
         auditRepository.createAuditLog(auditLog)
        return createdTask
    }
}