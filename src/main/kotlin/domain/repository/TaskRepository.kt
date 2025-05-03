package org.example.domain.repository


import org.example.domain.model.Task
import java.util.UUID

interface TaskRepository {

    fun createTask(task: Task): Result<Task>

    fun updateTask(task: Task, oldTask: Task, changedField: String): Result<Task>

    fun deleteTask(taskId: UUID): Result<Task>

    fun getTaskById(taskId: UUID): Result<Task>

    fun getTasks(): Result<List<Task>>
}