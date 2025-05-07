package org.example.domain.usecase.task

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend fun createTask(task: Task): Task = taskRepository.createTask(task)
}