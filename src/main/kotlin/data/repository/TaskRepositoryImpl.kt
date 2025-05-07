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
    override fun createTask(task: Task): Task {
        return try {
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
        } catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't create task. ${e.message}")
        }
    }

    override fun updateTask(task: Task, oldTask: Task, changedField: String): Task {
        return try {
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
        }catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't update task. ${e.message}")
        }
    }

    override fun deleteTask(taskId: UUID): Task {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            val taskLine = lines.find { taskCsvParser.parseCsvLine(it).taskId == taskId }
                ?: throw EiffelFlowException.IOException("Task not found")

            val task = taskCsvParser.parseCsvLine(taskLine)
            fileDataSource.deleteLineFromFile(taskLine)

            val auditLog = task.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.DELETE,
                changedField = null,
                oldValue = task.toString(),
                newValue = ""
            )
            auditRepository.createAuditLog(auditLog)
            task
        }catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't delete task with ID: $taskId because ${e.message}")
        }
    }

    override fun getTaskById(taskId: UUID): Task {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            lines.find { taskCsvParser.parseCsvLine(it).taskId == taskId }
                ?.let { taskCsvParser.parseCsvLine(it) }
                ?: throw EiffelFlowException.NotFoundException("Task not found")
        }catch (e: Exception) {
            throw EiffelFlowException.NotFoundException("Can't get task with ID: $taskId because ${e.message}")
        }
    }

    override fun getTasks(): List<Task> {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            if (lines.isEmpty()) {
                throw EiffelFlowException.NotFoundException("No tasks found in the database. Please create a new task first.")
            }
            lines.map { taskCsvParser.parseCsvLine(it) }

        } catch (e: EiffelFlowException.NotFoundException) {
            throw e
        }catch (e: Exception) {
            throw EiffelFlowException.IOException("Can't get tasks because ${e.message}")
        }
    }

    companion object {
        const val FILE_NAME: String = "tasks.csv"
    }
}
