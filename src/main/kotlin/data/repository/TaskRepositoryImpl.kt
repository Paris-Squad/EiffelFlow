package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import org.example.domain.model.entities.Task
import org.example.domain.model.entities.User
import org.example.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource,
    private val auditDataSource: AuditDataSource,
) : TaskRepository {
    override fun createTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task, oldTask: Task, editor: User, changedField: String): Result<Task> {
        return taskDataSource.updateTask(task = task).also { result ->
            result.onSuccess { updatedTask ->
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
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        return taskDataSource.deleteTask(taskId)
    }

    override fun getTaskById(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun getTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }
}