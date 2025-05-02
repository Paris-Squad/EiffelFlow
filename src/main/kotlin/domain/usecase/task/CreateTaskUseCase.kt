package org.example.domain.usecase.task

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    fun createTask(task: Task): Result<Task> {
        return taskRepository.createTask(task)
    }
}