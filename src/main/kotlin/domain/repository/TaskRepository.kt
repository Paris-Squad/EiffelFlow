package org.example.domain.repository

import org.example.domain.model.Task
import java.util.UUID

interface TaskRepository {
    fun createTask(task: Task): Task
    fun updateTask(task: Task): Task
    fun deleteTask(taskId: UUID): Task
    fun getTasks(): List<Task>
}