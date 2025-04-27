package org.example.data.respoitory

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.*

class TaskRepositoryImpl : TaskRepository {
    override fun createTask(task: Task): Task {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task): Task {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: UUID): Task {
        TODO("Not yet implemented")
    }

    override fun getTasks(): List<Task> {
        TODO("Not yet implemented")
    }
}