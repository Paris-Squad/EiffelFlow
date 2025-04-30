package org.example.domain.usecase.task

import org.example.domain.model.entities.Task
import org.example.domain.repository.TaskRepository

class EditTaskUseCase(private val taskRepository: TaskRepository, ) {

    fun editTask(request: Task): Result<Task> {
        TODO("Not yet implemented")
    }
}
