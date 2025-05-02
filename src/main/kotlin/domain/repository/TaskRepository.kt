package org.example.domain.repository


import org.example.domain.model.Task
import org.example.domain.model.User
import java.util.UUID

interface TaskRepository {
    fun createTask(task: Task): Result<Task>
    fun updateTask(task: Task, oldTask: Task, editor: User, changedField: String): Result<Task>
    fun deleteTask(taskId: UUID): Result<Task>
    fun getTaskById(taskId: UUID): Result<Task>
    fun getTasks(): Result<List<Task>>
}