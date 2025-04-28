package org.example.data.storge.task

import org.example.domain.model.entities.Task
import java.util.UUID

interface TaskDataSource {

    fun createTask(task: Task): Result<Task>

    fun updateTask(task: Task): Result<Task>

    fun deleteTask(taskId: UUID): Result<Task>

    fun getTaskById(taskId: UUID): Result<Task>

    fun getTasks(): Result<List<Task>>
}