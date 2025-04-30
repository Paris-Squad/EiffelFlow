package org.example.domain.usecase.task

import org.example.domain.model.entities.Task
import org.example.domain.repository.TaskRepository

class CreateTaskUseCase(
    val taskRepository: TaskRepository
) {
    fun createTask(task: Task): Result<Task> {
        val createTaskResult = taskRepository.createTask(task)

        return createTaskResult.fold(
            onSuccess = { createdTask ->
                Result.success(createdTask)
            },
            onFailure = { throwable ->
                Result.failure(throwable)
            }
        )
    }
}