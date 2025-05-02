package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.TaskCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskCsvParser: TaskCsvParser,
    private val fileDataSource: FileDataSource,
    private val auditRepository: AuditRepository,
) : TaskRepository {
    override fun createTask(task: Task): Result<Task> {
        return runCatching {
            val csvLine = taskCsvParser.serialize(task)
            fileDataSource.writeLinesToFile(csvLine)
            val auditLog = task.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.CREATE,
                changedField = null,
                oldValue = null,
                newValue = task.title
            )
            auditRepository.createAuditLog(auditLog)
            task
        }.recoverCatching {
            throw EiffelFlowException.IOException("Can't create task. ${it.message}")
        }
    }

    override fun updateTask(task: Task, oldTask: Task, changedField: String): Result<Task> {
        return runCatching {
            val taskCsv = taskCsvParser.serialize(task)
            val oldTaskCsv = taskCsvParser.serialize(oldTask)
            fileDataSource.updateLinesToFile(taskCsv, oldTaskCsv)
            val auditLog = task.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.UPDATE,
                changedField = changedField,
                oldValue = oldTask.toString(),
                newValue = task.toString()
            )
            auditRepository.createAuditLog(auditLog)
            task
        }.recoverCatching {
            throw EiffelFlowException.IOException("Can't update task. ${it.message}")
        }
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        return runCatching {
            val lines = fileDataSource.readLinesFromFile()
            val taskLine = lines.find { taskCsvParser.parseCsvLine(it).taskId == taskId }
                ?: return Result.failure(EiffelFlowException.NotFoundException("Task not found"))

            val task = taskCsvParser.parseCsvLine(taskLine)
            fileDataSource.deleteLineFromFile(taskLine)
            task
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("Can't delete task with ID: $taskId because ${it.message}")
        }
    }

    override fun getTaskById(taskId: UUID): Result<Task> {
        return runCatching {
            val lines = fileDataSource.readLinesFromFile()
            val task = lines.find { taskCsvParser.parseCsvLine(it).taskId == taskId }
            if (task != null) {
                taskCsvParser.parseCsvLine(task)
            } else {
                throw EiffelFlowException.NotFoundException("Task not found")
            }
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("Can't get task with ID: $taskId because ${it.message}")
        }
    }

    override fun getTasks(): Result<List<Task>> {
        return runCatching {
            val lines = fileDataSource.readLinesFromFile().map { taskCsvParser.parseCsvLine(it) }
            lines.ifEmpty {
                throw EiffelFlowException.NotFoundException("No tasks found in the database. Please create a new task first.")
            }
        }.recoverCatching {
          throw EiffelFlowException.IOException("Can't get tasks because ${it.message}")
        }
    }

    companion object {
        const val FILE_NAME: String = "tasks.csv"
    }
}