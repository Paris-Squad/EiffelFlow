package org.example.domain.usecase.task

import org.example.domain.model.entities.Task
import org.example.domain.repository.TaskRepository
import java.util.*

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {

    fun deleteTask(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
    }
}
