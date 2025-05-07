package org.example.domain.repository


import org.example.domain.model.Task
import java.util.UUID

interface TaskRepository {

    fun createTask(task: Task): Task

    fun updateTask(task: Task, oldTask: Task, changedField: String): Task

    fun deleteTask(taskId: UUID): Task

    fun getTaskById(taskId: UUID): Task

    fun getTasks(): List<Task>
}