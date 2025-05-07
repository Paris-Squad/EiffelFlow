package data.mongorepository

import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID

class MongoTaskRepositoryImpl : TaskRepository {
    override suspend fun createTask(task: Task): Task {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(
        task: Task,
        oldTask: Task,
        changedField: String
    ): Task {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: UUID): Task {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(taskId: UUID): Task {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): List<Task> {
        TODO("Not yet implemented")
    }
}