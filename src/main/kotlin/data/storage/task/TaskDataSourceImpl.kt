package org.example.data.storage.task

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.TaskCsvMapper
import org.example.domain.model.Task
import org.example.domain.exception.EiffelFlowException
import java.util.UUID

class TaskDataSourceImpl(
    private val taskMapper: TaskCsvMapper,
    private val csvManager: CsvStorageManager
) : TaskDataSource {
    override fun createTask(task: Task): Result<Task> {
        return try {
            val csvLine = taskMapper.mapTo(task)
            csvManager.writeLinesToFile(csvLine)
            Result.success(task)
        } catch (exception: Exception) {
            Result.failure( EiffelFlowException.IOException(exception.message))
        }
    }

    override fun updateTask(task: Task, oldTask: Task): Result<Task> {
        return try {
            val taskCsv = taskMapper.mapTo(task)
            val oldTaskCsv = taskMapper.mapTo(oldTask)
            csvManager.updateLinesToFile(taskCsv, oldTaskCsv)
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(EiffelFlowException.IOException("Failed to update task: $e"))
        }
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        return try {
            val lines = csvManager.readLinesFromFile()
            val taskLine = lines.find { taskMapper.mapFrom(it).taskId == taskId }
                ?: return Result.failure(EiffelFlowException.NotFoundException("Task not found"))

            val task = taskMapper.mapFrom(taskLine)
            csvManager.deleteLineFromFile(taskLine)

            Result.success(task)
        } catch (e: Exception) {
            Result.failure(EiffelFlowException.IOException(e.message))
        }
    }

    override fun getTaskById(taskId: UUID): Result<Task> {
        val lines = csvManager.readLinesFromFile()
        val task = lines.find { taskMapper.mapFrom(it).taskId == taskId }
        return if (task != null) {
            Result.success(taskMapper.mapFrom(task))
        } else {
            Result.failure(EiffelFlowException.NotFoundException("Task not found"))
        }
    }

    override fun getTasks(): Result<List<Task>> {
        val lines = csvManager.readLinesFromFile().map { taskMapper.mapFrom(it) }
        return if (lines.isNotEmpty()) {
            Result.success(lines)
        } else {
            Result.failure(EiffelFlowException.NotFoundException("Task not found"))
        }
    }

    companion object {
        const val FILE_NAME: String = "tasks.csv"
    }

}