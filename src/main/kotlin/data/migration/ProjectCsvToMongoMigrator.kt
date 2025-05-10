package org.example.data.migration

import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository

class ProjectCsvToMongoMigrator(
    private val csvRepository: ProjectRepository,
    private val mongoRepository: ProjectRepository
) {
    suspend fun migrateProjects(): Boolean {
        try {
            val csvProjects = csvRepository.getProjects()

            csvProjects.forEach {
                mongoRepository.createProject(it)
            }
            return true
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("migrateProjects from CSV to Mongo Failed because ${exception.message}")
        }
    }
}