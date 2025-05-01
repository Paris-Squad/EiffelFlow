package org.example.data.storage.task

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.TaskCsvMapper
import org.example.domain.model.entities.Task
import org.example.domain.model.exception.EiffelFlowException
import java.util.UUID

class TaskDataSourceImpl(
    private val taskMapper: TaskCsvMapper,
    private val csvManager: CsvStorageManager
) : TaskDataSource {
    override fun createTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task, oldTask: Task): Result<Task> {
        return try {
            val taskCsv = taskMapper.mapTo(task)
            val oldTask = taskMapper.mapTo(oldTask)
            csvManager.updateLinesToFile(taskCsv, oldTask)
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: UUID): Result<Task> {
        val lines = csvManager.readLinesFromFile()
        val task = lines.find { taskMapper.mapFrom(it).taskId == taskId }
        return if (task != null) {
            Result.success(taskMapper.mapFrom(task))
        } else {
            Result.failure(EiffelFlowException.TaskNotFoundException("Task not found"))
        }
    }

    override fun getTasks(): Result<List<Task>> {
        val lines = csvManager.readLinesFromFile().map { taskMapper.mapFrom(it) }
        return if (lines.isNotEmpty()) {
            Result.success(lines)
        } else {
            Result.failure(EiffelFlowException.TaskNotFoundException("Task not found"))
        }
    }

    companion object {
        const val FILE_NAME: String = "tasks.csv"
    }

}