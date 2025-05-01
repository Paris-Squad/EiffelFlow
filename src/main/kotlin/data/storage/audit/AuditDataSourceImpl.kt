package org.example.data.repository

import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.example.domain.model.entities.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID
import org.example.domain.model.entities.User // Make sure to import User
import org.example.domain.model.audit.AuditLog // Make sure to import AuditLog
import org.example.domain.model.audit.AuditAction // Make sure to import AuditAction
import kotlinx.datetime.Clock // Make sure to import Clock
import kotlinx.datetime.TimeZone // Make sure to import TimeZone

class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource,
    private val auditDataSource: AuditDataSource,
) : TaskRepository {
    override fun createTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task, oldTask: Task, editor: User, changedField: String): Result<Task> {
        return taskDataSource.updateTask(task = task, oldTask = oldTask).also { result ->
            result.onSuccess { updatedTask ->
                createAuditLogForTaskUpdate(task, oldTask, editor, changedField, updatedTask)
            }
        }
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: UUID): Result<Task> {
        return taskDataSource.getTaskById(taskId)
    }

    override fun getTasks(): Result<List<Task>> {
        return taskDataSource.getTasks()
    }

    private fun createAuditLogForTaskUpdate(task: Task, oldTask: Task, editor: User, changedField: String, updatedTask: Task) {
        val auditLog = AuditLog(
            itemId = task.taskId,
            itemName = task.title,
            userId = editor.userId,
            editorName = editor.username,
            actionType = AuditAction.UPDATE,
            auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            changedField = changedField,
            oldValue = oldTask.toString(),
            newValue = updatedTask.toString()
        )
        auditDataSource.createAuditLog(auditLog)
    }
}