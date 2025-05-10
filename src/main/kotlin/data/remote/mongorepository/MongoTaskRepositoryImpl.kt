package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoTaskDto
import org.example.data.remote.mapper.TaskMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.utils.getFieldChanges
import java.util.*

class MongoTaskRepositoryImpl(
    database: MongoDatabase,
    private val auditRepository: AuditRepository,
    private val taskMapper: TaskMapper
) : TaskRepository {

    private val tasksCollection = database.getCollection<MongoTaskDto>(collectionName = MongoCollections.TASKS)

    override suspend fun createTask(task: Task): Task {
        try {
            val taskDto = taskMapper.toDto(task)
            tasksCollection.insertOne(taskDto)
            logAction(task, AuditLogAction.CREATE)
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
            val oldTask = taskMapper.fromDto(oldTaskDto)
            val fieldChanges = oldTask.getFieldChanges(task)
            val changedFieldsNames = fieldChanges.map { it.fieldName }
            val oldValues = fieldChanges.map { it.oldValue }
            val newValues = fieldChanges.map { it.newValue }
            logAction(
                task = task,
                actionType = AuditLogAction.UPDATE,
                changedField = changedFieldsNames.toString(),
                oldValue = oldValues.toString(),
                newValue = newValues.toString(),
            )
            return task
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't update Task with id ${task.projectId} because ${exception.message}")
        }
    }

    override suspend fun deleteTask(taskId: UUID): Task {
        try {
            val query = eq(MongoTaskDto::_id.name, taskId.toString())
            val deletedTaskDto = tasksCollection.findOneAndDelete(query)
            deletedTaskDto ?: throw EiffelFlowException.NotFoundException("Task with id $taskId not found")

            val deletedTask = taskMapper.fromDto(deletedTaskDto)

            logAction(
                task = deletedTask,
                actionType = AuditLogAction.DELETE
            )
            return deletedTask

        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't delete Task with id $taskId because ${exception.message}")
        }
    }

    override suspend fun getTaskById(taskId: UUID): Task {
        try {
            val query = eq(MongoTaskDto::_id.name, taskId.toString())
            val taskDto = tasksCollection.find(query).firstOrNull()
            taskDto ?: throw EiffelFlowException.NotFoundException("Task with id $taskId not found")
            return taskMapper.fromDto(taskDto)

        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Task with id $taskId because ${exception.message}")
        }
    }

    override suspend fun getTasks(): List<Task> {
        try {
            val tasksDto = tasksCollection.find().toList()
            return tasksDto.map { taskMapper.fromDto(it) }
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Tasks because ${exception.message}")
        }
    }

    private suspend fun logAction(
        task: Task,
        actionType: AuditLogAction,
        changedField: String? = null,
        oldValue: String? = null,
        newValue: String = task.toString()
    ) {
        val auditLog = task.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = actionType,
            changedField = changedField,
            oldValue = oldValue,
            newValue = newValue,
        )
        auditRepository.createAuditLog(auditLog)
    }
}