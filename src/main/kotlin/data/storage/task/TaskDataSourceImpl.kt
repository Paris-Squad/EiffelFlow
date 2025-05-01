package org.example.data.storage.task

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.Mapper
import org.example.data.storage.mapper.StateCsvMapper
import org.example.domain.model.entities.Task
import org.example.domain.model.exception.EiffelFlowException
import java.util.UUID

class TaskDataSourceImpl(
    private val taskMapper: Mapper<String, Task>,
    private val stateCsvMapper: StateCsvMapper,
    private val csvManager: CsvStorageManager
) : TaskDataSource {
    override fun createTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
            return try {
                val lines = csvManager.readLinesFromFile()
                    .filter { it.isNotBlank() }

                val tasks = lines.map { line -> taskMapper.mapFrom(line) }

                val taskToDelete = tasks.find { it.taskId == taskId }
                    ?: return Result.failure(EiffelFlowException.TaskNotFoundException())

                val updatedTasks = tasks.filterNot { it.taskId == taskId }

                val updatedLines = updatedTasks.map { taskMapper.mapTo(it) }.joinToString("\n")
                csvManager.clearFile()
                csvManager.writeLinesToFile(updatedLines)

                Result.success(taskToDelete)
            } catch (e: Exception) {
                Result.failure(e)
            }

    }

    override fun getTaskById(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun getTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    companion object {
        const val FILE_NAME: String = "tasks.csv"
    }

}