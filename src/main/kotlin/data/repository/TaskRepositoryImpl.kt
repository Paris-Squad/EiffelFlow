package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import org.example.domain.model.Task
import org.example.domain.model.User
import org.example.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource,
    private val auditDataSource: AuditDataSource,
) : TaskRepository {
    override fun createTask(task: Task): Result<Task> {
        val createdTask = taskDataSource.createTask(task)

        return createdTask.fold(
            onSuccess = {
                val auditLog = AuditLog(
                    itemId = task.taskId,
                    itemName = task.title,
                    userId = task.creatorId,
                    editorName = "Admin",
                    actionType = AuditLogAction.CREATE,
                    auditTime = task.createdAt,
                    changedField = null,
                    oldValue = null,
                    newValue = task.title
                )

                return auditDataSource.createAuditLog(auditLog).fold(
                    onSuccess = { Result.success(task) },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { return Result.failure(it) }
        )
    }


    override fun updateTask(task: Task, oldTask: Task, editor: User, changedField: String): Result<Task> {
        return taskDataSource.updateTask(task = task, oldTask = oldTask).also { result ->
            result.onSuccess { updatedTask ->
                createAuditLogForTaskUpdate(task, oldTask, editor, changedField, updatedTask)
            }
        }
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        return taskDataSource.deleteTask(taskId)
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
            actionType = AuditLogAction.UPDATE,
            auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            changedField = changedField,
            oldValue = oldTask.toString(),
            newValue = updatedTask.toString()
        )
        auditDataSource.createAuditLog(auditLog)
    }
}