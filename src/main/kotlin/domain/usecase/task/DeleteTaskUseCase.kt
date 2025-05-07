package org.example.domain.usecase.task

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.*

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {

    suspend fun deleteTask(taskId: UUID): Task = taskRepository.deleteTask(taskId)
}