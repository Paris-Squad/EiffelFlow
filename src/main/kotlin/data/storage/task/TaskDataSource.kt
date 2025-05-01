package org.example.data.storage.task

import org.example.domain.model.entities.Task
import java.util.UUID

interface TaskDataSource {

    fun createTask(task: Task): Result<Task>

    fun updateTask(task: Task , oldTask: Task): Result<Task>

    fun deleteTask(taskId: UUID): Result<Task>

    fun getTaskById(taskId: UUID): Result<Task>

    fun getTasks(): Result<List<Task>>
}