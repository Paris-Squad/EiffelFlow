package org.example.data.local.csvrepository

import org.example.data.BaseRepository
import org.example.data.local.FileDataSource
import org.example.data.local.parser.TaskCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskCsvParser: TaskCsvParser,
    private val fileDataSource: FileDataSource
) : BaseRepository(), TaskRepository {
    override suspend fun createTask(task: Task): Task {
        return executeSafely {
            val csvLine = taskCsvParser.serialize(task)
            fileDataSource.writeLinesToFile(csvLine)
            task
        }
    }

    override suspend fun updateTask(task: Task, oldTask: Task, changedField: String): Task {
        return executeSafely {
            val taskCsv = taskCsvParser.serialize(task)
            val oldTaskCsv = taskCsvParser.serialize(oldTask)
            fileDataSource.updateLinesToFile(taskCsv, oldTaskCsv)
            task
        }
    }

    override suspend fun deleteTask(taskId: UUID): Task {
        return executeSafely {
            val lines = fileDataSource.readLinesFromFile()
            val taskLine = lines.find { taskCsvParser.parseCsvLine(it).taskId == taskId }
                ?: throw EiffelFlowException.IOException("Task not found")

            val task = taskCsvParser.parseCsvLine(taskLine)
            fileDataSource.deleteLineFromFile(taskLine)
            task
        }
    }

    override suspend fun getTaskById(taskId: UUID): Task {
        return executeSafely {
            val lines = fileDataSource.readLinesFromFile()
            lines.find { taskCsvParser.parseCsvLine(it).taskId == taskId }
                ?.let { taskCsvParser.parseCsvLine(it) }
                ?: throw EiffelFlowException.NotFoundException("Task not found")
        }
    }

    override suspend fun getTasks(): List<Task> {
        return executeSafely {
            val lines = fileDataSource.readLinesFromFile()
            if (lines.isEmpty()) {
                throw EiffelFlowException.NotFoundException("No tasks found in the database. Please create a new task first.")
            }
            lines.map { taskCsvParser.parseCsvLine(it) }

        }
    }

    companion object {
        const val FILE_NAME: String = "tasks.csv"
    }
}
