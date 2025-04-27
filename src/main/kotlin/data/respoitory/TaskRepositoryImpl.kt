package org.example.data.respoitory

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import org.example.data.storge.DataSource
import java.util.*

class TaskRepositoryImpl(
    private val dataSource: DataSource<Task>
) : TaskRepository {
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