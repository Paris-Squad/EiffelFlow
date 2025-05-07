package org.example.domain.repository


import org.example.domain.model.Task
import java.util.UUID

interface TaskRepository {

    suspend fun createTask(task: Task): Task

    suspend fun updateTask(task: Task, oldTask: Task, changedField: String): Task

    suspend fun deleteTask(taskId: UUID): Task

    suspend fun getTaskById(taskId: UUID): Task

    suspend fun getTasks(): List<Task>
}