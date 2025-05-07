package data.mongorepository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.MongoCollections
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

class MongoTaskRepositoryImpl(
    private val database: MongoDatabase,
    private val auditRepository: AuditRepository
) : TaskRepository {

    private val tasksCollection = database.getCollection<Task>(collectionName = MongoCollections.TASKS)

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