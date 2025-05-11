package org.example.domain.usecase.task

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID

class GetTaskUseCase(
    private val taskRepository: TaskRepository ,
) {
    suspend fun getTasks() : List<Task>{
        return taskRepository.getTasks()
    }

    suspend fun getTaskByID(taskId: UUID): Task{
        return taskRepository.getTaskById(taskId)
    }
}