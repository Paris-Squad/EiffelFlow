package data.repository

import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import org.example.domain.model.entities.Task
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
                    userName = "Admin",
                    actionType = AuditAction.CREATE,
                    auditTime = task.createdAt,
                    changedField = null,
                    oldValue = null,
                    newValue = task.title
                )
                auditDataSource.createAuditLog(auditLog).fold(
                    onSuccess = { Result.success(task) },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun updateTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: UUID): Result<Task>{
        TODO("Not yet implemented")
    }

    override fun getTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }
}