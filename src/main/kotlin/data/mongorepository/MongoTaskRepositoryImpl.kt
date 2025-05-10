package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.MongoCollections
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID

class MongoTaskRepositoryImpl(
    database: MongoDatabase
) : TaskRepository {

    private val tasksCollection = database.getCollection<Task>(collectionName = MongoCollections.TASKS)

    override suspend fun createTask(task: Task): Task {
        try {
            val existingTask = tasksCollection.find(eq("taskId", task.taskId)).firstOrNull()
            if (existingTask != null) {
                throw EiffelFlowException.IOException("Task with taskId ${task.taskId} already exists")
            }
            tasksCollection.insertOne(task)
            return task
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't create Task because ${exception.message}")
        }
    }

    override suspend fun updateTask(
        task: Task,
        oldTask: Task,
        changedField: String
    ): Task {
        try {
            val updates = Updates.combine(
                Updates.set(Task::title.name, task.title),
                Updates.set(Task::description.name, task.description),
                Updates.set(Task::creatorId.name, task.creatorId),
                Updates.set(Task::projectId.name, task.projectId),
                Updates.set(Task::assignedId.name, task.assignedId),
                Updates.set(Task::state.name, task.state),
                Updates.set(Task::role.name, task.role)
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq("taskId", task.taskId)
            val oldTask = tasksCollection.findOneAndUpdate(query, updates, options)

            if (oldTask == null) {
                throw EiffelFlowException.NotFoundException("Task with id ${task.projectId} not found")
            }

            return task
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't update Task with id ${task.projectId} because ${exception.message}")
        }
    }

    override suspend fun deleteTask(taskId: UUID): Task {
        try {
            val query = eq("taskId", taskId)
            val deletedTask = tasksCollection.findOneAndDelete(query)

            if (deletedTask == null) {
                throw EiffelFlowException.NotFoundException("Task with id $taskId not found")
            }

            return deletedTask

        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't delete Task with id $taskId because ${exception.message}")
        }
    }

    override suspend fun getTaskById(taskId: UUID): Task {
        try {
            val task = tasksCollection.find(eq("taskId", taskId)).firstOrNull()
            return task ?: throw EiffelFlowException.NotFoundException("Task with id $taskId not found")
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Task with id $taskId because ${exception.message}")
        }
    }

    override suspend fun getTasks(): List<Task> {
        try {
            return tasksCollection.find().toList()
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Tasks because ${exception.message}")
        }
    }

}