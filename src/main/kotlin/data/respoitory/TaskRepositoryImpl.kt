package org.example.data.respoitory

import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.task.TaskDataSource
import org.example.domain.model.entities.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource,
    private val auditDataSource: AuditDataSource,
) : TaskRepository {
    override fun createTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
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