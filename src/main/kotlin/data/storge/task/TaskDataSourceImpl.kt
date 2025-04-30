package org.example.data.storge.task

import org.example.data.storge.CsvStorageManager
import org.example.data.storge.Mapper
import org.example.data.storge.mapper.StateCsvMapper
import org.example.domain.model.entities.Task
import java.util.*

class TaskDataSourceImpl(
    private val taskMapper: Mapper<List<String>, Task>,
    private val stateCsvMapper: StateCsvMapper,
    private val csvManager: CsvStorageManager
) : TaskDataSource {
    override fun createTask(task: Task): Result<Task> {
        return try {
            val stateAsString = stateCsvMapper.mapTo(task.state)
            val csvLine = taskMapper.mapTo(task).joinToString(",") + "$stateAsString\n"
            csvManager.writeLinesToFile(csvLine)
            Result.success(task)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun updateTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: UUID): Result<Task> {
        TODO("Not yet implemented")
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