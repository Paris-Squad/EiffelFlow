package org.example.data.migration

import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository

class TaskCsvToMongoMigrator(
    private val csvRepository: TaskRepository,
    private val mongoRepository: TaskRepository
) {
    suspend fun migrateTasks(): Boolean {
        try {
            val csvTasks = csvRepository.getTasks()

            csvTasks.forEach {
                mongoRepository.createTask(it)
            }
            return true
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("migrateTasks from CSV to Mongo Failed because ${exception.message}")
        }
    }
}