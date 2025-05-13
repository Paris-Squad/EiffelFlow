package data.remote.repository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.BaseRepository
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoTaskDto
import org.example.data.remote.mapper.TaskMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    database: MongoDatabase,
    private val taskMapper: TaskMapper
) : BaseRepository(), TaskRepository {

    private val tasksCollection = database.getCollection<MongoTaskDto>(collectionName = MongoCollections.TASKS)

    override suspend fun createTask(task: Task): Task {
        return executeSafely {
            val taskDto = taskMapper.toDto(task)
            tasksCollection.insertOne(taskDto)
            task
        }
    }

    override suspend fun updateTask(
        task: Task,
        oldTask: Task,
        changedField: String
    ): Task {
        return executeSafely {
            val taskDto = taskMapper.toDto(task)
            val updates = Updates.combine(
                Updates.set(MongoTaskDto::title.name, taskDto.title),
                Updates.set(MongoTaskDto::description.name, taskDto.description),
                Updates.set(MongoTaskDto::creatorId.name, taskDto.creatorId),
                Updates.set(MongoTaskDto::projectId.name, taskDto.projectId),
                Updates.set(MongoTaskDto::assignedId.name, taskDto.assignedId),
                Updates.set(MongoTaskDto::stateId.name, taskDto.stateId),
                Updates.set(MongoTaskDto::stateName.name, taskDto.stateName),
                Updates.set(MongoTaskDto::role.name, taskDto.role)
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq(MongoTaskDto::_id.name, taskDto._id)
            val oldTaskDto = tasksCollection.findOneAndUpdate(query, updates, options)

            oldTaskDto ?: throw EiffelFlowException.NotFoundException("Task with id ${task.projectId} not found")
            task
        }
    }

    override suspend fun deleteTask(taskId: UUID): Task {
        return executeSafely {
            val query = eq(MongoTaskDto::_id.name, taskId.toString())
            val deletedTaskDto = tasksCollection.findOneAndDelete(query)
            deletedTaskDto ?: throw EiffelFlowException.NotFoundException("Task with id $taskId not found")

            val deletedTask = taskMapper.fromDto(deletedTaskDto)
            deletedTask
        }
    }

    override suspend fun getTaskById(taskId: UUID): Task {
        return executeSafely {
            val query = eq(MongoTaskDto::_id.name, taskId.toString())
            val taskDto = tasksCollection.find(query).firstOrNull()
            taskDto ?: throw EiffelFlowException.NotFoundException("Task with id $taskId not found")
            taskMapper.fromDto(taskDto)
        }
    }

    override suspend fun getTasks(): List<Task> {
        return executeSafely {
            val tasksDto = tasksCollection.find().toList()
            tasksDto.map { taskMapper.fromDto(it) }
        }
    }

}